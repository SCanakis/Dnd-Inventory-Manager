package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public class CharacterStatRequestMessage extends WebSocketMessage{
    
    public CharacterStatRequestMessage() {
        super("CHARACTER_STAT_REQUEST", null);
    }
    
    public CharacterStatRequestMessage(UUID charUuid) {
        super("CHARACTER_STAT_REQUEST", charUuid);
    }
}
