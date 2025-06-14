package com.scanakispersonalprojects.dndapp.model.inventory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.RollType;

public class Item {
    
    private UUID itemUuid;

    private String itemName;

    private String itemDescription;

    
    private Integer itemWeight = 0;

    
    private Integer itemValue = 0;

    
    private Rarity itemRarity = Rarity.common;

    
    private boolean attackable = false;
    
    private Short acBonus;
    
    private Map<AbilityScore, Boolean> addToAc;
    
    private boolean equippable = false;
    
    private boolean attunable = false;
    
    private List<EquippableType> itemEquippableTypes;
    
    private Map<AbilityScore, Integer> abilityRequirement;
    
    private Map<Skill, RollType> skillAlteredRollType;
    
    private Map<Skill, Integer> skillAlteredBonus;

}
