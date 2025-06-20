package com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Composite primary key for the CharactreHasItemSlot entity.
 */
@Embeddable
public class CharacterHasItemSlotId implements Serializable{
    
    @Column(name = "item_uuid")
    private UUID itemUuid;

    @Column(name = "character_uuid")
    private UUID charUuid;

    @Column(name = "container_uuid")
    private UUID containerUuid;

    public CharacterHasItemSlotId() {}

    public CharacterHasItemSlotId(UUID itemUuid, UUID charUuid, UUID containerUuid) {
        this.itemUuid = itemUuid;
        this.charUuid = charUuid;
        this.containerUuid = containerUuid;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public UUID getCharUuid() {
        return charUuid;
    }

    public void setCharUuid(UUID chaUuid) {
        this.charUuid = chaUuid;
    }

    

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CharacterHasItemSlotId) {
            CharacterHasItemSlotId other = (CharacterHasItemSlotId) obj;
            return Objects.equals(other.charUuid, this.charUuid) &&
                Objects.equals(other.itemUuid, this.itemUuid) &&
                Objects.equals(other.containerUuid, this.containerUuid);
        }   
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemUuid, charUuid);
    }

    public UUID getContainerUuid() {
        return containerUuid;
    }

    public void setContainerUuid(UUID containerUuid) {
        this.containerUuid = containerUuid;
    }


    

}
