package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public abstract class WebSocketMessage {

    private String type;
    private UUID charUuid;
    private long timestamp;
    
    public WebSocketMessage() {
        this.timestamp = System.currentTimeMillis();
    }

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
