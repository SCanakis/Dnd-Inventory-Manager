package com.scanakispersonalprojects.dndapp.model.webSocket;

public class WebSocketResponse {
    
    private String type;
    private boolean success;
    private String message;
    private Object data;
    private long timestamp;

    public WebSocketResponse() {
        this.timestamp = System.currentTimeMillis();
    }

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
