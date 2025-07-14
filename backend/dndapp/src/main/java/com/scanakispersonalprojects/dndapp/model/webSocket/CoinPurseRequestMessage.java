package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public class CoinPurseRequestMessage extends WebSocketMessage {
    
    public CoinPurseRequestMessage() {
        super("COIN_PURSE_REQUEST", null);
    }
    
    public CoinPurseRequestMessage(UUID charUuid) {
        super("COIN_PURSE_REQUEST", charUuid);
    }

}
