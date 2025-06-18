package com.scanakispersonalprojects.dndapp.service.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemSlotId;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.persistance.inventory.InventoryJPARepo;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {
    
    private InventoryJPARepo repo;
    private ItemCatalogService itemCatalogService;
    private final static UUID emptyContainerUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public InventoryService(InventoryJPARepo repo, ItemCatalogService itemCatalogService) {
        this.repo = repo;
        this.itemCatalogService = itemCatalogService;
    }

    public List<CharacterHasItemProjection> getInventoryWithUUID(UUID charUuid) {
        return this.repo.getInventoryUsingUUID(charUuid);
    }

    public List<CharacterHasItemProjection> getInventoryUsingFZF(UUID charUuid, String searchTerm) {
        return this.repo.getInventoyUsingFZF(charUuid, searchTerm);
    }

    @Transactional
    public boolean saveItemToInventory(UUID itemUuid, UUID charUuid, int quantity) {
        try {

            CharacterHasItemSlotId id = new CharacterHasItemSlotId(itemUuid, charUuid, emptyContainerUuid);        
            CharacterHasItemSlot slot = new CharacterHasItemSlot();
            slot.setId(id);

            ItemCatalog item = itemCatalogService.getItemWithUUID(itemUuid);
            if(item.isEquippable()) {
                slot.setEquipped(false);
            } 
            if(item.isAttunable()) {
                slot.setAttuned(false);
            } 
            if(quantity > 0) {
                slot.setQuantity(quantity);
            } else {
                slot.setQuantity(1);
            }
            repo.save(slot);

            return true;

        } catch (Exception e) {
            return false;
        }
        
    }

}
