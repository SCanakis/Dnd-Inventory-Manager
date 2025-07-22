package com.scanakispersonalprojects.dndapp.model.coinPurse;


/**
 * Data Transfer Object (DTO) dfor updating coin purse information.
 *  
 * This class reprseents the five standard currenty type. 
 * 
 * Fileds initialized to -1 indicated no update should be perfomred. 
 * 
 */
public class CoinPurseDTO {
    
    private int platinum = -1;
    
    private int gold = -1;

    private int electrum = -1;

    private int silver = -1;

    private int copper = -1;

    public CoinPurseDTO() {}

    public int getPlatinum() {
        return platinum;
    }

    public void setPlatinum(int platinum) {
        this.platinum = platinum;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getElectrum() {
        return electrum;
    }

    public void setElectrum(int electrum) {
        this.electrum = electrum;
    }

    public int getSilver() {
        return silver;
    }

    public void setSilver(int silver) {
        this.silver = silver;
    }

    public int getCopper() {
        return copper;
    }

    public void setCopper(int copper) {
        this.copper = copper;
    }

    

}
