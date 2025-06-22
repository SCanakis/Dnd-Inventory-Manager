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

/**
 * Repository interface for ItemCatalog entity operations.
 * Provides database access methods for managing the master catalog of items
 * available in the D&D application, including search and retrieval operations.
 * 
 */
@Repository
public interface ItemCatalogRepo extends JpaRepository<ItemCatalog, UUID>{

    /**
     * Finds an item catalog entry by its unique identifier.
     * This is functionally equivalent to findById() but provides explicit naming.
     *
     * @param itemUuid the unique identifier of the item to find
     * @return Optional containing the ItemCatalog if found, empty Optional otherwise
     */
    Optional<ItemCatalog> findByItemUuid(UUID itemUuid);

    /**
     * Retrieves all items from the catalog as lightweight projections.
     * Returns only essential item information (UUID, name, weight, value, rarity)
     * without loading the full entity with all its complex properties.
     *
     * @return list of item projections for all catalog items, empty list if catalog is empty
     */
    List<ItemProjection> findAllBy();
    
    /**
     * Searches the item catalog using fuzzy string matching on item names.
     * Uses PostgreSQL's SIMILARITY function to find items with names similar
     * to the search term, with a minimum similarity threshold of 0.06.
     * Results are ordered by similarity score (most similar first).
     *
     * @param searchTerm the text to search for in item names
     * @return list of matching item projections ordered by similarity, empty list if no matches
     */
    @Query(value =  "SELECT item_uuid, item_name, item_weight, item_value, item_rarity " + 
                    "FROM item_catalog WHERE " +
                    "SIMILARITY(item_name, :searchTerm) > 0.06 " +
                    "ORDER BY SIMILARITY(item_name, :searchTerm) DESC;", 
           nativeQuery = true)
    List<ItemProjection> findByNameSimilarity(@Param("searchTerm") String searchTerm);


}
