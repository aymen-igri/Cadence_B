package com.education.education.exeption;

/**
 * Exception thrown when WebSocket authentication fails.
 * This includes cases where:
 * - JWT token is missing
 * - JWT token is invalid or malformed
 * - JWT token is expired
 * - Token validation fails for any reason
 *
 * This should result in a 401 Unauthorized HTTP status.
 */
public class WebSocketAuthenticationException extends RuntimeException {
    
    public WebSocketAuthenticationException(String message) {
        super(message);
    }
    
    public WebSocketAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
