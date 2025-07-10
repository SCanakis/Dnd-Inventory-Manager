package com.scanakispersonalprojects.dndapp.model.webSocket;

/**
 * Response model for WebSocket messages sent from the server to clients.
 */
public class WebSocketResponse {
    
    /**
     * The type of response (e.g., "CHARACTER_STAT_LOAD", "UNAUTHORIZED").
     */
    private String type;

    /**
     * Whether the operation was succesful
     */
    private boolean success;

    /**
     * A descriptive message about the response
     */
    private String message;

    /**
     * The reponse data payload (can be any object type).
     */
    private Object data;

    /**
     * The timestamp when this reposnse was created.
     */
    private long timestamp;

    /**
     * Default contructor that sets the timestamp to the current time.
     */
    public WebSocketResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a WebSocket reponse with all fields
     * 
     * @param type - the response type identifier
     * @param success - whther the operation was successful
     * @param message - a descriptive message
     * @param data - the response data payload
     */
    public WebSocketResponse(String type, boolean success, String message, Object data) {
        this.type = type;
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    

}
