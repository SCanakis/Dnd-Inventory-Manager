package com.scanakispersonalprojects.dndapp.persistance.inventory;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemProjection;

import java.util.List;


@Repository
public interface ItemCatalogJPARepo extends JpaRepository<ItemCatalog, UUID>{

    Optional<ItemCatalog> findByItemUuid(UUID itemUuid);

    List<ItemProjection> findAllBy();
    
    @Query(value =  "SELECT item_uuid, item_name, item_weight, item_value, item_rarity " + 
                    "FROM item_catalog WHERE " +
                    "SIMILARITY(item_name, :searchTerm) > 0.1 " +
                    "ORDER BY SIMILARITY(item_name, :searchTerm) DESC;", 
           nativeQuery = true)
    List<ItemProjection> findByNameSimilarity(@Param("searchTerm") String searchTerm);


}
