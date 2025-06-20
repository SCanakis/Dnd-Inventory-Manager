package com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;

import jakarta.persistence.*;

/**
 * Entity representing the relationship between items and the D&D classes
 * that are eligible to use them. This junction table enforces class
 * restrictions on items (e.g., only Paladins can use Holy Avengers,
 * only spellcasters can use arcane focuses).
 * 
 * Uses a composite primary key consisting of item UUID and class UUID
 * to create a many-to-many relationship between items and classes.
 */

@Entity
@Table(name ="item_class_eligibility")
public class ItemClassEligibility {
    @EmbeddedId
    private ItemClassEligibilityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_uuid",  insertable = false, updatable = false)
    private DndClass ClassEntity;

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

    public DndClass getClassEntity() {
        return ClassEntity;
    }

    public void setClassEntity(DndClass classEntity) {
        ClassEntity = classEntity;
    }
    
    public UUID getItemUuid() { return id != null ? id.getItemUuid() : null; }
    public UUID getClassUuid() { return id != null ? id.getClassUuid() : null; }


}
