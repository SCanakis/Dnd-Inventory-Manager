package com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name ="item_class_eligibility")
public class ItemClassEligibility {
    @EmbeddedId
    private ItemClassEligibilityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_uuid",  insertable = false, updatable = false)
    private ClassEntity ClassEntity;

    public ItemClassEligibility() {}
    
    public ItemClassEligibility(UUID itemUuid, UUID classUuid) {
        this.id = new ItemClassEligibilityId(itemUuid, classUuid);
    }

    public ItemClassEligibilityId getId() {
        return id;
    }

    public void setId(ItemClassEligibilityId id) {
        this.id = id;
    }

    public ClassEntity getClassEntity() {
        return ClassEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        ClassEntity = classEntity;
    }
    
    public UUID getItemUuid() { return id != null ? id.getItemUuid() : null; }
    public UUID getClassUuid() { return id != null ? id.getClassUuid() : null; }


}
