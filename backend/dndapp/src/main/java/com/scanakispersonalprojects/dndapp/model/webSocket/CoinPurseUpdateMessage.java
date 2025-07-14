package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurseDTO;

public class CoinPurseUpdateMessage extends WebSocketMessage{

    private CoinPurseDTO coinPurseDTO;

    public CoinPurseUpdateMessage() {
        super("COIN_PURSE_UPDATE", null);
    }

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
