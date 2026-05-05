package com.education.education.config;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.security.Principal;
import org.springframework.messaging.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;
import com.education.education.auth.utils.AuthUtils;
import com.education.education.exeption.WebSocketAuthenticationException;
import com.education.education.exeption.WebSocketAuthorizationException;
import com.education.education.groups.services.GroupAuthorizationService;
import com.education.education.user.user.wrapper.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private static final String WS_AUTH_ATTRIBUTE = "wsAuthentication";

    private final AuthUtils authUtils;
    private final UserDetailsService userDetailsService;
    private final GroupAuthorizationService groupAuthorizationService;

    @Bean
    public HandshakeInterceptor webSocketHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(
                    ServerHttpRequest request,
                    ServerHttpResponse response,
                    WebSocketHandler wsHandler,
                    Map<String, Object> attributes) {
                try {
                    UUID groupId = extractGroupId(request);
                    Authentication authentication = authenticateRequestIfPresent(request);

                    if (authentication == null) {
                        if (groupId != null) {
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return false;
                        }

                        return true;
                    }

                    attributes.put(WS_AUTH_ATTRIBUTE, authentication);

                    if (groupId != null) {
                        UUID userId = resolveUserId(authentication);
                        if (!groupAuthorizationService.isUserGroupMember(groupId, userId)) {
                            logger.warn("WebSocket handshake: User {} is not a member of group {}", userId, groupId);
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            return false;
                        }
                    }

                    return true;
                } catch (WebSocketAuthenticationException ex) {
                    logger.warn("WebSocket handshake authentication failed: {}", ex.getMessage());
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return false;
                } catch (WebSocketAuthorizationException ex) {
                    logger.warn("WebSocket handshake authorization failed: {}", ex.getMessage());
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    return false;
                } catch (IllegalArgumentException ex) {
                    logger.warn("WebSocket handshake rejected due to invalid parameters: {}", ex.getMessage());
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return false;
                }
            }

            @Override
            public void afterHandshake(
                    ServerHttpRequest request,
                    ServerHttpResponse response,
                    WebSocketHandler wsHandler,
                    Exception exception) {
                // no-op
            }
        };
    }

    @Bean
    public DefaultHandshakeHandler webSocketHandshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(
                    ServerHttpRequest request,
                    WebSocketHandler wsHandler,
                    Map<String, Object> attributes) {
                return (Principal) attributes.get(WS_AUTH_ATTRIBUTE);
            }
        };
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null) {
                    return message;
                }

                try {
                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        handleConnectCommand(accessor);
                    }

                    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                        handleSubscribeCommand(accessor);
                    }
                } catch (WebSocketAuthenticationException | WebSocketAuthorizationException ex) {
                    logger.warn("WebSocket authentication/authorization failed: {}", ex.getMessage());
                    throw ex;
                } catch (Exception ex) {
                    logger.error("Unexpected error in WebSocket interceptor: {}", ex.getMessage(), ex);
                    throw new WebSocketAuthenticationException("Unexpected error during WebSocket handshake", ex);
                }

                return message;
            }
        });
    }

    private void handleConnectCommand(StompHeaderAccessor accessor) {
        if (accessor.getUser() instanceof Authentication existingAuthentication) {
            SecurityContextHolder.getContext().setAuthentication(existingAuthentication);
            logger.debug("WebSocket CONNECT: User {} already authenticated during handshake",
                    existingAuthentication.getName());
            return;
        }

        List<String> authorization = accessor.getNativeHeader("Authorization");

        if (authorization == null || authorization.isEmpty()) {
            logger.warn("WebSocket CONNECT: Authorization header missing");
            throw new WebSocketAuthenticationException(
                    "Authorization header is missing. Please provide a valid JWT token.");
        }

        String bearToken = authorization.get(0);

        if (!bearToken.startsWith("Bearer ")) {
            logger.warn("WebSocket CONNECT: Authorization header does not start with 'Bearer '");
            throw new WebSocketAuthenticationException("Invalid authorization format. Expected 'Bearer <token>'");
        }

        String token = bearToken.substring(7);

        try {
            String username = authUtils.verifyDecodedJwtToken(token).getSubject();

            if (username == null || username.isEmpty()) {
                logger.warn("WebSocket CONNECT: Token subject (username) is null or empty");
                throw new WebSocketAuthenticationException("Invalid token: username not found");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
            accessor.setUser(auth);

            logger.debug("WebSocket CONNECT: User {} authenticated successfully", username);

        } catch (WebSocketAuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.warn("WebSocket CONNECT: Token validation failed - {}", ex.getMessage());
            throw new WebSocketAuthenticationException("Invalid or expired JWT token", ex);
        }
    }

    private void handleSubscribeCommand(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        if (destination == null || !destination.startsWith("/topic/groups/")) {
            return;
        }

        String[] parts = destination.split("/");
        if (parts.length < 4) {
            logger.warn("WebSocket SUBSCRIBE: Invalid topic destination format: {}", destination);
            throw new WebSocketAuthorizationException("Invalid topic destination format");
        }

        try {
            UUID groupId = UUID.fromString(parts[3]);

            Authentication authentication = resolveAuthentication(accessor);
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("WebSocket SUBSCRIBE: User not authenticated");
                throw new WebSocketAuthorizationException("User not authenticated");
            }

            Object principal = authentication.getPrincipal();
            if (!(principal instanceof UserDetailsImpl)) {
                logger.warn("WebSocket SUBSCRIBE: Principal is not UserDetailsImpl, got: {}",
                        principal != null ? principal.getClass().getSimpleName() : "null");
                throw new WebSocketAuthorizationException("Invalid user details format");
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            UUID userId = userDetails.user.getId();

            boolean isGroupMember = groupAuthorizationService.isUserGroupMember(groupId, userId);

            if (!isGroupMember) {
                logger.warn("WebSocket SUBSCRIBE: User {} is not a member of group {}", userId, groupId);
                throw new WebSocketAuthorizationException(
                        String.format("You are not a member of group %s", groupId));
            }

            logger.debug("WebSocket SUBSCRIBE: User {} subscribed to group {} topic", userId, groupId);

        } catch (IllegalArgumentException ex) {
            logger.warn("WebSocket SUBSCRIBE: Invalid group ID format in destination: {}", destination);
            throw new WebSocketAuthorizationException("Invalid group ID format", ex);
        } catch (WebSocketAuthorizationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("WebSocket SUBSCRIBE: Unexpected error during authorization: {}", ex.getMessage(), ex);
            throw new WebSocketAuthorizationException("Error validating group membership", ex);
        }
    }

    private Authentication authenticateRequestIfPresent(ServerHttpRequest request) {
        List<String> authorization = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        if (authorization == null || authorization.isEmpty()) {
            return null;
        }

        String bearToken = authorization.get(0);

        if (!bearToken.startsWith("Bearer ")) {
            throw new WebSocketAuthenticationException("Invalid authorization format. Expected 'Bearer <token>'");
        }

        String token = bearToken.substring(7);
        String username = authUtils.verifyDecodedJwtToken(token).getSubject();

        if (username == null || username.isEmpty()) {
            throw new WebSocketAuthenticationException("Invalid token: username not found");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private UUID extractGroupId(ServerHttpRequest request) {
        String groupId = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("groupId");

        if (groupId == null || groupId.isBlank()) {
            return null;
        }

        return UUID.fromString(groupId);
    }

    private UUID resolveUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.user.getId();
        }

        throw new WebSocketAuthorizationException("Invalid user details format");
    }

    private Authentication resolveAuthentication(StompHeaderAccessor accessor) {
        if (accessor.getUser() instanceof Authentication authentication) {
            return authentication;
        }

        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(webSocketHandshakeInterceptor())
                .setHandshakeHandler(webSocketHandshakeHandler())
                .setAllowedOriginPatterns("*");
    }
}
