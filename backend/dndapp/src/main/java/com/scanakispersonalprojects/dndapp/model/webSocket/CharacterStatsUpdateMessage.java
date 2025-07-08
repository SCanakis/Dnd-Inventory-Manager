package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfoUpdateDTO;

public class CharacterStatsUpdateMessage extends WebSocketMessage {
    
    private CharacterInfoUpdateDTO update;

    public CharacterStatsUpdateMessage() {
        super("CHARACTER_UPDATE_REQUEST", null);
    }
    
    public CharacterStatsUpdateMessage(UUID charUuid, CharacterInfoUpdateDTO update) {
        super("CHARACTER_UPDATE_REQUEST", charUuid);
        this.update = update;
    }

    public CharacterInfoUpdateDTO getUpdate() {
        return update;
    }

    public void setUpdate(CharacterInfoUpdateDTO update) {
        this.update = update;
    }


}
