package com.scanakispersonalprojects.dndapp.model.coinPurse;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entity represent a coin_purse sql table
 * 
 * The table incldue the follwowing fileds:
 * 
 * pk   char_info_uuid      UUID (fk)
 *      platinum            INT
 *      gold                INT
 *      electrum            INT
 *      silver              INT
 *      copper              INT
 */
@Entity
@Table(name = "coin_purse")
public class CoinPurse {
    
    @Id
    @Column(name = "char_info_uuid")
    private UUID charUuid;

    @Column(name = "platinum")
    private int platinum;
    
    @Column(name = "gold")
    private int gold;

    @Column(name = "electrum")
    private int electrum;

    @Column(name = "silver")
    private int silver;

    @Column(name = "copper")
    private int copper;

    @OneToOne
    @MapsId
    @JoinColumn(name="char_info_uuid")
    private CharacterInfo characterInfo;

    public CoinPurse() {

    }

    public CoinPurse(UUID charUuid, int platinum, int gold, int electrum, int silver, int copper) {
        this.charUuid = charUuid;
        this.platinum = platinum;
        this.gold = gold;
        this.electrum = electrum;
        this.silver = silver;
        this.copper = copper;
    }

    public UUID getcharUuid() {
        return charUuid;
    }

    public void setCharUucharUuid(UUID charUuid) {
        this.charUuid = charUuid;
    }

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

    public void setCharacterInfo(CharacterInfo characterInfo) {
        this.characterInfo = characterInfo;
    }

    public CharacterInfo getCharacterInfo() {
        return this.characterInfo;
    }
    
}
