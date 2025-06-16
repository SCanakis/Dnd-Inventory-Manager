package com.scanakispersonalprojects.dndapp.service.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.persistance.inventory.InventoryJPARepo;

@Service
public class InventoryService {
    
    private InventoryJPARepo repo;

    public InventoryService(InventoryJPARepo repo) {
        this.repo = repo;
    }

    public List<CharacterHasItemProjection> getInventoryWithUUID(UUID charUuid) {
        return this.repo.getInventoryUsingUUID(charUuid);
    }

}
