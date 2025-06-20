package com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Composite primary key for the ItemClassEligibility entity.
 * Uniquely identifies an item-class eligibility relationship by combining
 * the item UUID with the class UUID that is eligible to use it.
 * 
 * This design allows the same item to have multiple class restrictions
 * while preventing duplicate eligibility entries for the same item-class pair.
 */
@Embeddable
public class ItemClassEligibilityId implements Serializable{

    @Column(name = "item_uuid")
    private UUID itemUuid;

    @Column(name="class_uuid")
    private UUID classUuid;

    public ItemClassEligibilityId() {}

    
    public ItemClassEligibilityId(UUID itemUuid, UUID classUuid) {
        this.itemUuid = itemUuid;
        this.classUuid = classUuid;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        ItemClassEligibilityId that = (ItemClassEligibilityId) obj;
        return Objects.equals(itemUuid, that.itemUuid) && 
                Objects.equals(classUuid, that.classUuid);

    }

    @Override
    public int hashCode() {
        return Objects.hash(itemUuid, classUuid);
    }


    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public UUID getClassUuid() {
        return classUuid;
    }

    public void setClassUuid(UUID classUuid) {
        this.classUuid = classUuid;
    }

}
