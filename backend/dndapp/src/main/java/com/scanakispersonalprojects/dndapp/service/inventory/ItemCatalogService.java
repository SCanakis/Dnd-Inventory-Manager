package com.scanakispersonalprojects.dndapp.service.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ClassNameIdPair;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemProjection;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ItemCatalogRepo;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ItemClassEligibilityRepo;


/**
 * Service class for managing item catalog operations.
 * Handles business logic for retrieving, searching, and creating items
 * in the master catalog, including enrichment with class eligibility data.
 * 
 * Provides both full item details and lightweight projections for
 * different use cases throughout the application.
 */
@Service
public class ItemCatalogService {
    
    /** Repository for item catalog database operations */
    private ItemCatalogRepo repo;

    /** Repository for item class eligibility operations */
    private ItemClassEligibilityRepo classRepo;

    /**
     * Constructs a new ItemCatalogService with the required repository dependencies.
     *
     * @param repo repository for item catalog operations
     * @param classRepo repository for item class eligibility operations
     */
    public ItemCatalogService(ItemCatalogRepo repo, ItemClassEligibilityRepo classRepo) {
        this.repo = repo;
        this.classRepo = classRepo;
    }

    /**
     * Retrieves a complete item from the catalog by its UUID.
     * Enriches the item with class eligibility information by loading
     * all D&D classes that are eligible to use this item.
     *
     * @param itemUuid the unique identifier of the item to retrieve
     * @return the complete ItemCatalog with class eligibility data,
     *         or null if the item is not found
     */
    public ItemCatalog getItemWithUUID(UUID itemUuid) {
        Optional<ItemCatalog> itemCatalog = repo.findByItemUuid(itemUuid);
        if(itemCatalog.isEmpty()) {
            return null;
        } else {
            ItemCatalog item = itemCatalog.get();
            List<Object[]> classes = classRepo.findClassesByItemUuid(itemUuid);
            List<ClassNameIdPair> pairs = new ArrayList<>();
            for(Object[] row : classes) {
                pairs.add(new ClassNameIdPair((UUID) row[0], (String) row[1]));
            }
            item.setClassNameIdPair(pairs);
            return item;
        }
    }

    /**
     * Retrieves all items from the catalog as lightweight projections.
     * Returns only essential item information without complex properties
     * or relationships for efficient bulk operations.
     *
     * @return list of item projections for all catalog items,
     *         empty list if the catalog is empty
     */
    public List<ItemProjection> getAll() {
        return repo.findAllBy();
    }

    /**
     * Searches the item catalog using fuzzy string matching on item names.
     * Uses similarity-based search to find items with names similar to
     * the search term, ordered by relevance.
     *
     * @param string the search term to match against item names
     * @return list of matching item projections ordered by similarity,
     *         empty list if no matches are found
     */
    public List<ItemProjection> searchByName(String string) {
        return repo.findByNameSimilarity(string);
    }

    /**
     * Creates a new item in the catalog.
     * Saves the provided item to the database and returns the persisted entity.
     * Note: This method does not handle class eligibility relationships.
     *
     * @param item the ItemCatalog entity to create
     * @return the saved ItemCatalog entity with generated UUID and database state
     */
    public ItemCatalog createItem(ItemCatalog item) {
        return repo.save(item);
    }

}
