package com.scanakispersonalprojects.dndapp.testutils.inventory;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlot;

public class CharacterHasItemSlotBuilder {
    private UUID itemUuid;
    private UUID characterUuid;
    private UUID slotUuid;
    private Integer quantity;
    private Boolean isEquipped;
    private Boolean isAttuned;
    private Boolean isIdentified;
    
    public CharacterHasItemSlotBuilder itemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
        return this;
    }
    
    public CharacterHasItemSlotBuilder characterUuid(UUID characterUuid) {
        this.characterUuid = characterUuid;
        return this;
    }
    
    public CharacterHasItemSlotBuilder slotUuid(UUID slotUuid) {
        this.slotUuid = slotUuid;
        return this;
    }
    
    public CharacterHasItemSlotBuilder quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
    
    public CharacterHasItemSlotBuilder isEquipped(Boolean isEquipped) {
        this.isEquipped = isEquipped;
        return this;
    }
    
    public CharacterHasItemSlotBuilder isAttuned(Boolean isAttuned) {
        this.isAttuned = isAttuned;
        return this;
    }
    
    public CharacterHasItemSlotBuilder isIdentified(Boolean isIdentified) {
        this.isIdentified = isIdentified;
        return this;
    }
    
    public CharacterHasItemSlot build() {
        return new CharacterHasItemSlot(
            itemUuid,
            characterUuid,
            slotUuid,
            quantity,
            isEquipped,
            isAttuned,
            isIdentified
        );
    }
}

