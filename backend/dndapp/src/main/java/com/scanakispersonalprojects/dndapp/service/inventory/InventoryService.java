package com.scanakispersonalprojects.dndapp.service.inventory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlotId;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemUpdate;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.Container;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerId;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ContainerRepo;
import com.scanakispersonalprojects.dndapp.persistance.inventory.InventoryJPARepo;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {
    
    private InventoryJPARepo repo;
    private ItemCatalogService itemCatalogService;
    private ContainerRepo containerRepo;

    private final static UUID emptyContainerUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public InventoryService(InventoryJPARepo repo, ItemCatalogService itemCatalogService, ContainerRepo containerRepo) {
        this.repo = repo;
        this.itemCatalogService = itemCatalogService;
        this.containerRepo = containerRepo;
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

    @Transactional
    public CharacterHasItemSlot updateCharacterHasSlot(UUID charUuid, UUID itemUuid, UUID containerUuid, CharacterHasItemUpdate update) {

        if(charUuid == null || itemUuid == null) {
            return null;
        }

        CharacterHasItemSlotId id = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);
        Optional<CharacterHasItemSlot> slotOptional = repo.findById(id);
        
        update.setItemUuid(itemUuid);

        if(!slotOptional.isPresent()) {
            return null;
        }

        CharacterHasItemSlot slot = slotOptional.get();
        if (update.getQuantity() != null) {
        slot.setQuantity(update.getQuantity());
        }
        if (update.isEquipped() != null) {
            slot.setEquipped(update.isEquipped());
        }
        if (update.isAttuned() != null) {
            slot.setAttuned(update.isAttuned());
        }


        if (update.getContainerUuid() != null && !update.getContainerUuid().equals(containerUuid)) {

            if(!checkIfItemFitsInContainerAndUpdateContainer(charUuid, update.getContainerUuid(), update) ||  
            !removeItemFromContainer(charUuid, containerUuid, update)) {
                return null;
            } 

            UUID newContainerUuid = update.getContainerUuid();
            CharacterHasItemSlotId targetId = new CharacterHasItemSlotId(itemUuid, charUuid, newContainerUuid);
            Optional<CharacterHasItemSlot> targetSlotOptional = repo.findById(targetId);

            if(targetSlotOptional.isPresent()) {

                CharacterHasItemSlot targetSlot = targetSlotOptional.get();
                targetSlot.setQuantity(targetSlot.getQuantity() + slot.getQuantity());
                repo.delete(slot);
                return repo.save(targetSlot);

            } else {
                CharacterHasItemSlot newSlot = new CharacterHasItemSlot();
                newSlot.setId(new CharacterHasItemSlotId(itemUuid, charUuid, newContainerUuid));
                newSlot.setQuantity(slot.getQuantity());
                newSlot.setEquipped(slot.isEquipped());
                newSlot.setAttuned(slot.isAttuned());
                newSlot.setInAttackTab(slot.isInAttackTab());
                
                repo.delete(slot);  
                return repo.save(newSlot);  
            }
        }
        
        return repo.save(slot);
    }


    private boolean checkIfItemFitsInContainerAndUpdateContainer(UUID charUuid, UUID conatinerUuid, CharacterHasItemUpdate update) {
        Optional<Container> optionalContainer =  containerRepo.findById(new ContainerId(conatinerUuid, charUuid));

        ItemCatalog item = itemCatalogService.getItemWithUUID(update.getItemUuid());

        if(optionalContainer.isPresent() && item != null) {
            Container container = optionalContainer.get();
            int sum = update.getQuantity() * item.getItemWeight() + container.getCurrentCapacity();
            if(sum <= container.getMaxCapacity()) {
                containerRepo.updateCurrentCapacity(charUuid, conatinerUuid, sum);
                return true;
            }
        }
        return false;
    }

    private boolean removeItemFromContainer(UUID charUuid, UUID conatinerUuid, CharacterHasItemUpdate update) {
        Optional<Container> optionalContainer =  containerRepo.findById(new ContainerId(conatinerUuid, charUuid));

        ItemCatalog item = itemCatalogService.getItemWithUUID(update.getItemUuid());

        if(optionalContainer.isPresent() && item != null) {

            Container container = optionalContainer.get();
            int remainder = container.getCurrentCapacity() - update.getQuantity() * item.getItemWeight();

            if(remainder >= 0) {
                containerRepo.updateCurrentCapacity(charUuid, conatinerUuid, remainder);
                return true;
            }
        }
        return false;
    }
       
    

}
