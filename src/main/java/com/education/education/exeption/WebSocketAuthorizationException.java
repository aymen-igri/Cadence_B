package com.education.education.exeption;

/**
 * Exception thrown when WebSocket authorization fails.
 * This includes cases where:
 * - User is authenticated but not a member of the group they're trying to access
 * - User doesn't have permission to subscribe to a specific topic
 *
 * This should result in a 403 Forbidden HTTP status.
 */
public class WebSocketAuthorizationException extends RuntimeException {
    
    public WebSocketAuthorizationException(String message) {
        super(message);
    }
    
    public WebSocketAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
