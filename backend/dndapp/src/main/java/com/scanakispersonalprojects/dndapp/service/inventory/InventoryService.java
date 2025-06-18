package com.scanakispersonalprojects.dndapp.service.inventory;

import java.sql.SQLException;
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
        if (charUuid == null) {
            throw new IllegalArgumentException();
        }
        return this.repo.getInventoryUsingUUID(charUuid);
    }

    public List<CharacterHasItemProjection> getInventoryUsingFZF(UUID charUuid, String searchTerm) {
        if (charUuid == null) {
            throw new IllegalArgumentException();
        }
        return this.repo.getInventoyUsingFZF(charUuid, searchTerm);
    }

    @Transactional
    public boolean saveItemToInventory(UUID itemUuid, UUID charUuid, int quantity) {
        if (charUuid == null || itemUuid == null) {
            throw new IllegalArgumentException();
        }
        if(quantity <= 0) {
            return false;
        }

        try {
            
            List<CharacterHasItemSlot> exisitingSlots = repo.getListAnItemWithDifferntContainers(charUuid, itemUuid);

            if(!exisitingSlots.isEmpty()) {
                updateQuantity(charUuid, itemUuid, quantity);
            }

            CharacterHasItemSlotId id = new CharacterHasItemSlotId(itemUuid, charUuid, emptyContainerUuid);        
            CharacterHasItemSlot slot = new CharacterHasItemSlot();
            slot.setId(id);

            ItemCatalog item = itemCatalogService.getItemWithUUID(itemUuid);

            if(item == null) {
                return false;
            }

            if(item.isEquippable()) {
                slot.setEquipped(false);
            } 
            if(item.isAttunable()) {
                slot.setAttuned(false);
            } 

            repo.save(slot);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Boolean deleteItemFromInventory(UUID charUuid, UUID itemUuid, UUID containerUuid) throws Exception{
        
        if(charUuid == null || itemUuid == null) {
            return false;
        }
        
        try {
            CharacterHasItemSlotId slotId = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);

            if(repo.existsById(slotId)) {
                repo.deleteById(new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid));
                return true;
            }
            return null;

        } catch (Exception e) {
            new SQLException();
            return false;
        }
    }

    @Transactional
    public boolean updateQuantity(UUID charUuid, UUID itemUuid, int quantity) {
        if(charUuid == null || itemUuid == null) {
            return false;
        }
        
        try {
            List<CharacterHasItemSlot> slots = repo.getListAnItemWithDifferntContainers(charUuid, itemUuid);
            if(slots.isEmpty()) {
                return false;
            }
            CharacterHasItemSlotId id = slots.get(0).getId();

            CharacterHasItemSlot slot = repo.getReferenceById(id);
            slot.setQuantity(slot.getQuantity() + quantity);
            repo.save(slot);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
