package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * Absctract base class for all WebSocket messages.
 */
public abstract class WebSocketMessage {

    /**
     * Type of WebSocket message (e.g., "CHARACTER_STAT_REQUEST", "CONTAINER_DELETE").
     */
    private String type;

    /**
     * The UUID of the character associated with the message
     */
    private UUID charUuid;

    /**
     * The timestamp when this message was created.
     */
    private long timestamp;
    
    /**
     * Default contructor that sets the timestap to the current time.
     */
    public WebSocketMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a WebSocket message with the specified type and charcter UUID
     * 
     * @param type
     * @param charUuid
     */
    public WebSocketMessage(String type, UUID charUuid) {
        this.type = type;
        this.charUuid = charUuid;
        this.timestamp = System.currentTimeMillis();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getCharUuid() {
        return charUuid;
    }

    public void setCharUuid(UUID charUuid) {
        this.charUuid = charUuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    

    
}
