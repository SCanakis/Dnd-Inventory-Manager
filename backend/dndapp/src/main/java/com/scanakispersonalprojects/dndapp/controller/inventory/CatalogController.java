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





@Controller
@RequestMapping("itemCatalog")
public class CatalogController {
    
    private final static Logger LOG = Logger.getLogger(CatalogController.class.getName());

    private final ItemCatalogService itemCatalogService;
    private final CustomUserDetailsService userService;
    private final InventoryService inventoryService;

    private final static String getPath = "GET /itemCatalog/";
    private final static String postPath = "POST /itemCatalog/";
    
    
    public CatalogController(ItemCatalogService itemCatalogService, CustomUserDetailsService userService, InventoryService inventoryService) {
        this.itemCatalogService = itemCatalogService;
        this.userService = userService;
        this.inventoryService = inventoryService;
    }


    @GetMapping("/id={uuid}")
    public ResponseEntity<ItemCatalog> getItem(@PathVariable UUID uuid) {
        LOG.info(getPath +"id=" + uuid);
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


    @GetMapping
    public ResponseEntity<List<ItemProjection>> getAll() {
        LOG.info(getPath);
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

    @GetMapping("/searchTerm={searchTerm}")
    public ResponseEntity<List<ItemProjection>> searchByName(@PathVariable String searchTerm) {
        LOG.info(getPath + "/serachTerm=" + searchTerm);
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

    @PostMapping("/id={itemUuid}/charId={charUuid}")
    public ResponseEntity<Boolean> addItemToCharacterInventory(Authentication authentication, @PathVariable UUID itemUuid, @PathVariable UUID charUuid, @RequestParam int quantity) {
        LOG.info(postPath + "id=" + itemUuid + "/charId=" + charUuid);
        List<UUID> characters = userService.getUsersCharacters(authentication);
        
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
