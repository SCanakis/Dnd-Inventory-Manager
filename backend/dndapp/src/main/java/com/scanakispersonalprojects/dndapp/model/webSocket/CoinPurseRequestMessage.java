package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * WebSocket message model for requesting Coin Purse in real-time.
 */
public class CoinPurseRequestMessage extends WebSocketMessage {
    
    /**
     * Default contructor for creating a coin purse request message without character UUID.
     */
    public CoinPurseRequestMessage() {
        super("COIN_PURSE_REQUEST", null);
    }
    
    /**
     * Controctor for creatina a coin purse request message with a specific character UUID.
     * 
     * @param charUuid
     */
    public CoinPurseRequestMessage(UUID charUuid) {
        super("COIN_PURSE_REQUEST", charUuid);
    }

}
