package com.scanakispersonalprojects.dndapp.persistance.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemClassEligibility;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemClassEligibilityId;

/**
 * Repository interface for ItemClassEligibility entity operations.
 * Manages the many-to-many relationship between items and the D&D classes
 * that are eligible to use them, enforcing class restrictions on equipment.
 */
@Repository
public interface ItemClassEligibilityRepository extends JpaRepository<ItemClassEligibility, ItemClassEligibilityId>{
    
    /**
     * Retrieves all D&D classes that are eligible to use a specific item.
     * Joins the eligibility table with the class table to return both
     * class UUIDs and names for display purposes.
     * 
     * The returned Object[] contains:
     * - Index 0: class_uuid (UUID of the eligible class)
     * - Index 1: name (String name of the eligible class)
     *
     * @param itemUuid the unique identifier of the item to check eligibility for
     * @return list of Object arrays containing class UUID and name pairs,
     *         empty list if no class restrictions exist for the item
     */
    @Query(value = """
        SELECT c.class_uuid, c.name 
        FROM item_class_eligibility ice
        JOIN class c ON ice.class_uuid = c.class_uuid
        WHERE ice.item_uuid = :itemUuid
        """, nativeQuery = true)
    List<Object[]> findClassesByItemUuid(@Param("itemUuid") UUID itemUuid);
}
