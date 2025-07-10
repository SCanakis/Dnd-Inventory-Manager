package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * WebSocket message model for requesting character stats data
 */
public class CharacterStatRequestMessage extends WebSocketMessage{
    
    /**
     * Default contructor for creating a character stats request messagewithotu character UUID.
     */
    public CharacterStatRequestMessage() {
        super("CHARACTER_STAT_REQUEST", null);
    }
    
    /**
     * Contrucotr for creating a character stats reques message with a specific character UUID.
     * 
     * @param charUuid
     */
    public CharacterStatRequestMessage(UUID charUuid) {
        super("CHARACTER_STAT_REQUEST", charUuid);
    }
}
