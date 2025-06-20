package com.scanakispersonalprojects.dndapp.controller.inventory;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemUpdate;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.InventoryService;
import com.scanakispersonalprojects.dndapp.service.inventory.ItemCatalogService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;



/**
 * REST controller for managing character inventory operations in the D&D application.
 * Handles retrieving, searching, updating, and deleting items from character inventories.
 * All operations require the authenticated user to own the specified character.
 * 
 * Base path: /inventory/{uuid} where uuid is the character UUID
 */
@Controller
@RequestMapping("inventory/{uuid}")
public class InventoryController {
    
    /** Logger for this controller */
    private final Logger LOG = Logger.getLogger(InventoryController.class.getName());

    /** Service for inventory management operations */ 
    private InventoryService inventoryService;

    /** Service for user authentication and authorization */
    private CustomUserDetailsService userService;

    /** Service for user authentication and authorization */
    private ItemCatalogService itemCatalogService;

    private final static String GET_PATH = "GET /inventory/";
    private final static String DELETE_PATH = "DELETE /inventory/";
    private final static String PATCH_PATH = "PATCH /inventory/";

    /**
     * Constructs a new InventoryController with the required service dependencies.
     *
     * @param inventoryService service for inventory management operations
     * @param userService service for user authentication and authorization
     * @param itemCatalogService service for item catalog operations
     */
    public InventoryController(InventoryService inventoryService, CustomUserDetailsService userService, ItemCatalogService itemCatalogService) {
        this.inventoryService= inventoryService;
        this.userService = userService;
        this.itemCatalogService = itemCatalogService;
    }

    /**
     * Retrieves the complete inventory for a specific character.
     * Requires the authenticated user to own the character.
     *
     * @param authentication the authentication context of the current user
     * @param uuid the unique identifier of the character
     * @return ResponseEntity containing list of CharacterHasItemProjection (200 OK),
     *         401 UNAUTHORIZED if user doesn't own the character,
     *         404 NOT_FOUND if inventory doesn't exist,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */
    @GetMapping
    public ResponseEntity<List<CharacterHasItemProjection>> getInventoryUsingUUID(Authentication authentication, @PathVariable UUID uuid) {
        LOG.info(GET_PATH + uuid);
        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<CharacterHasItemProjection> inventory = inventoryService.getInventoryWithUUID(uuid);
            if(inventory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Searches the character's inventory using a fuzzy search term.
     * Performs partial matching on item names within the character's inventory.
     * Requires the authenticated user to own the character.
     *
     * @param authentication the authentication context of the current user
     * @param searchTerm the term to search for in inventory item names
     * @param uuid the unique identifier of the character
     * @return ResponseEntity containing list of matching CharacterHasItemProjection (200 OK),
     *         401 UNAUTHORIZED if user doesn't own the character,
     *         404 NOT_FOUND if no matches found,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */
    @GetMapping("/searchTerm={searchTerm}")
    public ResponseEntity<List<CharacterHasItemProjection>> getInventoryFuzzySearch(Authentication authentication, @PathVariable String searchTerm, @PathVariable UUID uuid) {
        LOG.info(GET_PATH + uuid + "/searchTerm="+ searchTerm);
        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        try {
            List<CharacterHasItemProjection> inventory = inventoryService.getInventoryUsingFZF(uuid, searchTerm);
            if(inventory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves detailed information about a specific item from the catalog.
     * This endpoint doesn't require character ownership validation as it only
     * returns catalog information, not character-specific inventory data.
     *
     * @param itemUuid the unique identifier of the item
     * @param uuid the unique identifier of the character (path context)
     * @return ResponseEntity containing ItemCatalog details (200 OK),
     *         404 NOT_FOUND if item doesn't exist,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */

    @GetMapping("/id={itemUuid}")
    public ResponseEntity<ItemCatalog> getItemFromInventory(@PathVariable UUID itemUuid, @PathVariable UUID uuid) {
        LOG.info(GET_PATH + uuid + "/id="+ itemUuid);

        try {
            ItemCatalog item = itemCatalogService.getItemWithUUID(itemUuid);
            if(item == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes an item from a character's inventory container.
     * Requires the authenticated user to own the character.
     *
     * @param authentication the authentication context of the current user
     * @param itemUuid the unique identifier of the item to remove
     * @param uuid the unique identifier of the character
     * @param containerUuid the unique identifier of the container holding the item
     * @return ResponseEntity containing true if deletion successful (200 OK),
     *         401 UNAUTHORIZED if user doesn't own the character,
     *         404 NOT_FOUND if item or container doesn't exist,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */
    @DeleteMapping("/id={itemUuid}/containerId={containerUuid}")
    public ResponseEntity<Boolean> deleteItemFromInventory(Authentication authentication, @PathVariable UUID itemUuid, @PathVariable UUID uuid, @PathVariable UUID containerUuid) {
        LOG.info(DELETE_PATH + uuid + "/id="+ itemUuid + "/containerId=" + containerUuid);

        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        try {
            if(inventoryService.deleteItemFromInventory(uuid, itemUuid, containerUuid) != null &&
                inventoryService.deleteItemFromInventory(uuid, itemUuid, containerUuid) != false ) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Updates properties of an item slot in a character's inventory.
     * Allows modification of quantity, position, or other item slot attributes.
     * Requires the authenticated user to own the character.
     *
     * @param authentication the authentication context of the current user
     * @param uuid the unique identifier of the character
     * @param itemUuid the unique identifier of the item to update
     * @param containerUuid the unique identifier of the container holding the item
     * @param update the update data containing new values for the item slot
     * @return ResponseEntity containing updated CharacterHasItemSlot (200 OK),
     *         401 UNAUTHORIZED if user doesn't own the character,
     *         404 NOT_FOUND if item or container doesn't exist,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */
    @PatchMapping("/id={itemUuid}/containerId={containerUuid}")
    public ResponseEntity<CharacterHasItemSlot> characterHasItemUpdate(Authentication authentication, @PathVariable UUID uuid, @PathVariable UUID itemUuid, @PathVariable UUID containerUuid, @RequestBody CharacterHasItemUpdate update ) {
        LOG.info(PATCH_PATH + uuid + "/id="+ itemUuid + "/containerId=" + containerUuid);

        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        try {
            CharacterHasItemSlot result = inventoryService.updateCharacterHasSlot(uuid, itemUuid, containerUuid, update);

            if(result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    




}
