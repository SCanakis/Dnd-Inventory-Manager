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




@Controller
@RequestMapping("inventory/{uuid}")
public class InventoryController {
    
    private final Logger LOG = Logger.getLogger(InventoryController.class.getName());

    private InventoryService inventoryService;
    private CustomUserDetailsService userService;
    private ItemCatalogService itemCatalogService;
    private final static String GET_PATH = "GET /inventory/";
    private final static String DELETE_PATH = "DELETE /inventory/";
    private final static String PATCH_PATH = "PATCH /inventory/";

    public InventoryController(InventoryService inventoryService, CustomUserDetailsService userService, ItemCatalogService itemCatalogService) {
        this.inventoryService= inventoryService;
        this.userService = userService;
        this.itemCatalogService = itemCatalogService;
    }



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
