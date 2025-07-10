package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfoUpdateDTO;


/**
 * WebSocket message modle for updating character stats in real-time.
 */
public class CharacterStatsUpdateMessage extends WebSocketMessage {
    
    // Update data containnig the fileds to be changed
    private CharacterInfoUpdateDTO update;

    /**
     * Default constructor for JSON deserilization
     */
    public CharacterStatsUpdateMessage() {
        super("CHARACTER_UPDATE_REQUEST", null);
    }
    
    /**
     * Creats an update message for a specific characteer
     * 
     * @param charUuid - charUuid of character to be updates
     * @param update - the update data containig fields to change
     */
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
