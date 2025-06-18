package com.scanakispersonalprojects.dndapp.model.inventory;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="character_has_item_slot")
public class CharacterHasItemSlot {
    
    @EmbeddedId
    private CharacterHasItemSlotId id;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    @Column(name = "equipped")
    private boolean equipped;

    @Column(name = "attuned")
    private boolean attuned;

    @Column(name = "in_attack_tab")
    private boolean inAttackTab;

    public CharacterHasItemSlot() {}

    public CharacterHasItemSlot(UUID itemUuid, UUID charUuid, UUID slotUuid, int quantity, boolean equipped, boolean attuned,
            boolean inAttackTab) {

        this.id = new CharacterHasItemSlotId(itemUuid, charUuid, slotUuid);
        this.quantity = quantity;
        this.equipped = equipped;
        this.attuned = attuned;
        this.inAttackTab = inAttackTab;
    }

    public CharacterHasItemSlotId getId() {
        return id;
    }

    public void setId(CharacterHasItemSlotId id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    public boolean isAttuned() {
        return attuned;
    }

    public void setAttuned(boolean attuned) {
        this.attuned = attuned;
    }

    public boolean isInAttackTab() {
        return inAttackTab;
    }

    public void setInAttackTab(boolean inAttackTab) {
        this.inAttackTab = inAttackTab;
    }


    
    
}
