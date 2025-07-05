package com.scanakispersonalprojects.dndapp.controller.inventory;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemProjection;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.InventoryService;
import com.scanakispersonalprojects.dndapp.service.inventory.ItemCatalogService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * REST controller for managing item catalog operations in the Dnd application.
 * Proves endpoints for retrieving items, searchign the catalog, adding items
 * to character inventories, and creating new items (admin only).
 * 
 * Base path: /itemCatalog
 */

@Controller
@RequestMapping("itemCatalog")
public class CatalogController {
    
    /** Logger for this controller */
    private final static Logger LOG = Logger.getLogger(CatalogController.class.getName());

    /** Service for item catalog operation */
    private final ItemCatalogService itemCatalogService;
    
    /** Service for user authentication and authorization */
    private final CustomUserDetailsService userService;
    
    /** Service for inventory managment operation */
    private final InventoryService inventoryService;

    private final static String GET_PATH = "GET /itemCatalog/";
    private final static String POST_PATH = "POST /itemCatalog/";
    
    /**
     * Constructs a new CatalogController with the requires service dependencies
     * 
     * @param itemCatalogService
     * @param userService
     * @param inventoryService
     */
    public CatalogController(ItemCatalogService itemCatalogService, CustomUserDetailsService userService, InventoryService inventoryService) {
        this.itemCatalogService = itemCatalogService;
        this.userService = userService;
        this.inventoryService = inventoryService;
    }


    /**
     * Retrieves a specific item form the catalog by its UUID
     * 
     * @param uuid - the idnetifier of the item to be retrieved
     * @return      - Response entity containing ItemCatalog if found(200 OK)
     *              - 404 NOT_FOUND if null
     *              - 500 INTERNAL_SERVER_ERROR if exception occurs
     */

    @GetMapping("/id={uuid}")
    public ResponseEntity<ItemCatalog> getItem(@PathVariable UUID uuid) {
        LOG.info(GET_PATH +"id=" + uuid);
        try {
            ItemCatalog item = itemCatalogService.getItemWithUUID(uuid);
            if(item == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all items from the catalog as projections.
     * Returns a simplified view of all the catalog items.
     * 
     * @return      - Response entity containing List<ItemProjection> if found(200 OK)
     *              - 404 NOT_FOUND if null
     *              - 500 INTERNAL_SERVER_ERROR if exception occurs
     */

    @GetMapping
    public ResponseEntity<List<ItemProjection>> getAll() {
        LOG.info(GET_PATH);
        try {
            List<ItemProjection> items = itemCatalogService.getAll();
            if(items == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Searches the item catalog by name using a search term.
     * Performs a partail match search on item names. Using a 
     * fuzzy finder
     * 
     * @param searchTerm the term to search for in item names
     * @return      - Response entity containing List<ItemProjection> if found(200 OK)
     *              - 404 NOT_FOUND if null
     *              - 500 INTERNAL_SERVER_ERROR if exception occurs
     */
    @GetMapping("/searchTerm={searchTerm}")
    public ResponseEntity<List<ItemProjection>> searchByName(@PathVariable String searchTerm) {
        LOG.info(GET_PATH + "/serachTerm=" + searchTerm);
        try {
            List<ItemProjection> items = itemCatalogService.searchByName(searchTerm);
            if(items == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds an item from the catalog to a character's inventory.
     * Requies the authenticated user to won the specified character.
     * 
     * @param authentication - the authetncation context of the current user
     * @param itemUuid - the item identifier
     * @param charUuid - the character identifier
     * @param quantity - the number of items to add to the user
     * @return          - ResponseEntity containing true if succesfful (200 OK)
     *                  - 401 UNAUTHORIZED
     *                  - 404 NOT FOUND on null
     *                  - 500 INTERNAL_SERVER_ERROR on exception 
     */

    @PostMapping("/id={itemUuid}/charId={charUuid}")
    public ResponseEntity<Boolean> addItemToCharacterInventory(Authentication authentication, @PathVariable UUID itemUuid, @PathVariable UUID charUuid, @RequestParam int quantity) {
        LOG.info(POST_PATH + "id=" + itemUuid + "/charId=" + charUuid);
        List<UUID> characters = userService.getUsersCharacters(authentication);
        
        LOG.info("=== ADD ITEM REQUEST ===");
        LOG.info("Item UUID: " + itemUuid.toString());
        LOG.info("Character UUID: " + charUuid.toString());
        LOG.info("Quantity received: " + quantity);
        LOG.info("========================");

        if(!characters.contains(charUuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        try {
            boolean result = inventoryService.saveItemToInventory(itemUuid, charUuid, quantity);
            if(result) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new item in the catalog. Restriced to admin users only
     * 
     * 
     * @param authentication - the authentication context of the user
     * @param itemCatalog - the item data to create
     * @return      - Response entity containing ItemCatalog if found(200 OK)
     *              - 401 UNAUTHORIZED if user is not an admin
     *              - 404 NOT_FOUND if null
     *              - 500 INTERNAL_SERVER_ERROR if exception occurs
     */
    @PostMapping
    public ResponseEntity<ItemCatalog> createItem(Authentication authentication, @RequestBody ItemCatalog itemCatalog) {

        if(!userService.isAdmin(authentication)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            ItemCatalog item = itemCatalogService.createItem(itemCatalog);
            if(item != null) {
                return new ResponseEntity<>(item, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }    

    }
    

}
