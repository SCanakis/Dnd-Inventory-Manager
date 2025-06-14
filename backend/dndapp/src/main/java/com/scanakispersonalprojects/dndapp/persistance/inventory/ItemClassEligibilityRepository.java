package com.scanakispersonalprojects.dndapp.persistance.inventory;

import com.scanakispersonalprojects.dndapp.model.inventory.ItemClassEligibility;
import com.scanakispersonalprojects.dndapp.model.inventory.ItemClassEligibilityId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemClassEligibilityRepository extends JpaRepository<ItemClassEligibility, ItemClassEligibilityId>{
    
    @Query(value = """
        SELECT c.class_uuid, c.name 
        FROM item_class_eligibility ice
        JOIN class c ON ice.class_uuid = c.class_uuid
        WHERE ice.item_uuid = :itemUuid
        """, nativeQuery = true)
    List<Object[]> findClassesByItemUuid(@Param("itemUuid") UUID itemUuid);
}
