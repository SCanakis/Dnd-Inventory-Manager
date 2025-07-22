package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurseDTO;


/**
 * WebScoket message model for updating coin purse in real-time
 */
public class CoinPurseUpdateMessage extends WebSocketMessage{

    // updates data containing the feilds to be changed
    private CoinPurseDTO coinPurseDTO;

    /**
     * Default contructor for JSON deseralization
     */
    public CoinPurseUpdateMessage() {
        super("COIN_PURSE_UPDATE", null);
    }

    /**
     * Creates an update message for a coin purse
     * 
     * @param charUuid - charUuid for character to be updates
     * @param coinPurseDTO - the update data containing fields to be changed
     */
    public CoinPurseUpdateMessage(UUID charUuid, CoinPurseDTO coinPurseDTO ) {
        super("COIN_PURSE_UPDATE", charUuid);
        this.coinPurseDTO = coinPurseDTO;
    }

    public CoinPurseDTO getCoinPurseDTO() {
        return coinPurseDTO;
    }

    public void setCoinPurseDTO(CoinPurseDTO coinPurseDTO) {
        this.coinPurseDTO = coinPurseDTO;
    }

}
