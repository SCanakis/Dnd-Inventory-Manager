package com.scanakispersonalprojects.dndapp.model.basicCharInfo;


/**
 * This an enum used in {@link DndClass}
 * for a classes particuarl hitDice.
 * 
 */
public enum HitDiceValue {
    
    D4(4),
    D6(6),
    D8(8),
    D10(10),
    D12(12);
    
    private final int value;

    HitDiceValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
