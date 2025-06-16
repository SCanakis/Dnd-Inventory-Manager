package com.scanakispersonalprojects.dndapp.controller.inventory;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("inventory")
public class InventoryController {
    
    private final Logger LOG = Logger.getLogger(InventoryController.class.getName());

    private InventoryService inventoryService;
    private CustomUserDetailsService userService;

    public InventoryController(InventoryService inventoryService, CustomUserDetailsService userService) {
        this.inventoryService= inventoryService;
        this.userService = userService;
    }



    @GetMapping("/{uuid}")
    public ResponseEntity<List<CharacterHasItemProjection>> getInventoryUsingUUID(Authentication authentication, @PathVariable UUID uuid) {
        LOG.info("GET /inventory/" + uuid);
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


}
