package com.scanakispersonalprojects.dndapp.service.inventory;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
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
import com.scanakispersonalprojects.dndapp.persistance.inventory.InventoryRepo;

import jakarta.transaction.Transactional;

/**
 * Service class for managing character inventory operations.
 * Handles business logic for adding, removing, updating, and searching items
 * in character inventories, including container management and item transfers.
 * 
 * Manages complex operations like item stacking, container transfers,
 * and automatic container creation for container items.
 */
@Service
public class InventoryService {
    
    /** Repository for inventory item operations */
    private InventoryRepo repo;

    /** Service for item catalog operations */
    private ItemCatalogService itemCatalogService;
    
    /** Repository for container operations */
    private ContainerRepo containerRepo;

    /** 
     * Special UUID representing the default inventory space.
     * Items placed in this "container" are in the character's base inventory.
     */
    private final static UUID emptyContainerUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

    /**
     * Constructs a new InventoryService with the required dependencies.
     *
     * @param repo repository for inventory operations
     * @param itemCatalogService service for item catalog operations
     * @param containerRepo repository for container operations
     */
    public InventoryService(InventoryRepo repo, ItemCatalogService itemCatalogService, ContainerRepo containerRepo) {
        this.repo = repo;
        this.itemCatalogService = itemCatalogService;
        this.containerRepo = containerRepo;
    }



    /**
     * Retrieves the complete inventory for a character.
     *
     * @param charUuid the unique identifier of the character
     * @return list of character inventory item projections
     * @throws IllegalArgumentException if charUuid is null
     */
    public List<CharacterHasItemProjection> getInventoryWithUUID(UUID charUuid) {
        if (charUuid == null) {
            throw new IllegalArgumentException();
        }
        return this.repo.getInventoryUsingUUID(charUuid);
    }


    /**
     * Searches a character's inventory using fuzzy string matching.
     *
     * @param charUuid the unique identifier of the character
     * @param searchTerm the text to search for in item names
     * @return list of matching inventory items ordered by similarity
     * @throws IllegalArgumentException if charUuid is null
     */
    public List<CharacterHasItemProjection> getInventoryUsingFZF(UUID charUuid, String searchTerm) {
        if (charUuid == null) {
            throw new IllegalArgumentException();
        }
        return this.repo.getInventoyUsingFZF(charUuid, searchTerm);
    }

    public List<CharacterHasItemProjection> getContainerItemsUsingFZF(UUID charUuid, UUID containerUuid, String searchTerm) {
        if(charUuid == null || containerUuid == null || searchTerm == null) {
            return null;
        }
        return this.repo.getItemsInContainerUsingFZF(charUuid, containerUuid, searchTerm);
    }

    public List<CharacterHasItemProjection> getItemsInContainer(UUID charUuid, UUID containerUuid) {
        if(charUuid == null || containerUuid == null) {
            return null;
        }
        return this.repo.getItemsForAContainer(charUuid, containerUuid);
    }


    /**
     * Adds an item to a character's inventory.
     * If the item is a container, automatically creates a corresponding Container entity.
     * If the character already has the item, updates the quantity instead.
     *
     * @param itemUuid the unique identifier of the item to add
     * @param charUuid the unique identifier of the character
     * @param quantity the number of items to add (must be positive)
     * @return true if the item was successfully added, false otherwise
     * @throws IllegalArgumentException if any required parameter is null or quantity is non-positive
     */
    @Transactional
    public boolean saveItemToInventory(UUID itemUuid, UUID charUuid, int quantity) {
        if (charUuid == null || itemUuid == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }

        try {
            
            ItemCatalog item = itemCatalogService.getItemWithUUID(itemUuid);
 
            if(item != null) {

                if(item.isContainer() && item.getCapacity() > 0 && item.getCapacity() != null) {
                    Container container = new Container(null, charUuid, itemUuid, item.getCapacity(), 0);
                    containerRepo.save(container);
                    if(updateInventoryCapacity(item, charUuid, 1)) {
                        return true;
                    } else {
                        return false;
                    }
                }

                List<CharacterHasItemSlot> exisitingSlots = repo.getSameItemDifferentContainers(charUuid, itemUuid);

                if(!exisitingSlots.isEmpty()) {
                    updateQuantity(charUuid, itemUuid, quantity+exisitingSlots.get(0).getQuantity());
                }

                if(!updateInventoryCapacity(item, charUuid, quantity)) {
                    return false;
                }

                CharacterHasItemSlotId id = new CharacterHasItemSlotId(itemUuid, charUuid, emptyContainerUuid);        
                CharacterHasItemSlot slot = new CharacterHasItemSlot();
                slot.setId(id);

                if(item.isEquippable()) {
                    slot.setEquipped(false);
                } 
                if(item.isAttunable()) {
                    slot.setAttuned(false);
                } 
                if(item.isAttackable()) {
                    slot.setInAttackTab(false);
                }

                slot.setQuantity(quantity);


                repo.save(slot);

                return true;
            }
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateInventoryCapacity(ItemCatalog item, UUID charUuid, int quantity) {
        Optional<Container> optionalContainer =  containerRepo.findById(new ContainerId(emptyContainerUuid, charUuid));
                
        if(optionalContainer.isPresent() && item != null) {
            Container container = optionalContainer.get();
            int sum = quantity * item.getItemWeight() + container.getCurrentCapacity();
            if(sum <= container.getMaxCapacity()) {
                containerRepo.updateCurrentCapacity(charUuid, emptyContainerUuid, sum);
                return true;
            }
        } 
        return false;
    }


    /**
     * Removes an item from a character's inventory.
     * Deletes the entire item slot regardless of quantity.
     *
     * @param charUuid the unique identifier of the character
     * @param itemUuid the unique identifier of the item to remove
     * @param containerUuid the unique identifier of the container holding the item
     * @return true if deletion successful, null if item not found, false on error
     * @throws SQLException if a database error occurs during deletion
     */
    @Transactional
    public Boolean deleteItemFromInventory(UUID charUuid, UUID itemUuid, UUID containerUuid) throws Exception{
        
        if(charUuid == null || itemUuid == null) {
            return false;
        }
        
        try {
            CharacterHasItemSlotId slotId = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);

            if(repo.existsById(slotId)) {
                CharacterHasItemSlot slot = repo.getReferenceById(slotId);
                
                Optional<Container> optionalContainer =  containerRepo.findById(new ContainerId(containerUuid, charUuid));
                
                ItemCatalog item = itemCatalogService.getItemWithUUID(itemUuid);
                
                if(item.isContainer()) {
                    List<Container> containers= containerRepo.getCharactersContainers(charUuid);

                    for(Container container : containers) {
                        if(Objects.equals(container.getItemUuid(), itemUuid)) {
                            if(container.getCurrentCapacity() > 0) {
                                return null;
                            } else {
                                containerRepo.deleteById(container.getId());
                            }
                        }
                    }
                    
                }
                
                repo.deleteById(new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid));

                if(optionalContainer.isPresent() && item != null) {
                    Container container = optionalContainer.get();
                    int sum = container.getCurrentCapacity() - slot.getQuantity() * item.getItemWeight();
                    if(sum >= 0) {
                        containerRepo.updateCurrentCapacity(charUuid, containerUuid, sum);
                    }
                } else {
                    return false;
                }


                return true;
            }
            return null;

        } catch (Exception e) {
            throw new SQLException();
        }
    }


    /**
     * Updates the quantity of an existing item in inventory.
     * Adds the specified quantity to the first found slot of the item.
     *
     * @param charUuid the unique identifier of the character
     * @param itemUuid the unique identifier of the item to update
     * @param quantity the quantity to add (can be negative to reduce)
     * @return true if update successful, false if item not found or error occurs
     */
    @Transactional
    public boolean updateQuantity(UUID charUuid, UUID itemUuid, int quantity) {
        if(charUuid == null || itemUuid == null) {
            return false;
        }
        
        try {
            List<CharacterHasItemSlot> slots = repo.getSameItemDifferentContainers(charUuid, itemUuid);
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


    /**
     * Updates properties of a character's item slot.
     * Handles both simple property updates and complex container transfers.
     * Container transfers can be partial (splitting stacks) or complete.
     *
     * @param charUuid the unique identifier of the character
     * @param itemUuid the unique identifier of the item to update
     * @param containerUuid the current container holding the item
     * @param update the update data containing new values
     * @return the updated CharacterHasItemSlot, or null if update failed
     */
    @Transactional
    public CharacterHasItemSlot updateCharacterHasSlot(UUID charUuid, UUID itemUuid, UUID containerUuid, CharacterHasItemUpdate update) {

        if(charUuid == null || itemUuid == null || update == null) {
            return null;
        }
        CharacterHasItemSlotId id;
        if(containerUuid != null) {
            id = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);
        } else {
            id = new CharacterHasItemSlotId(itemUuid, charUuid, emptyContainerUuid);
        }

        Optional<CharacterHasItemSlot> slotOptional = repo.findById(id);
        
        update.setItemUuid(itemUuid);

        if(!slotOptional.isPresent()) {
            return null;
        }

        CharacterHasItemSlot slot = slotOptional.get();
        ItemCatalog item = itemCatalogService.getItemWithUUID(itemUuid);

        if(item.isContainer()) {
            return null;
        }

        if(update.getQuantity() != null && update.getContainerUuid() != null && !Objects.equals(update.getContainerUuid(), containerUuid)) {
            
            if(slot.getQuantity() < update.getQuantity() || update.getQuantity() < 0) {
                return null;
            }

            if(slot.getQuantity() > update.getQuantity()) {
                return partialContainerTransfer(charUuid, itemUuid, containerUuid, update, slot);

            } else {
                return completeContainerTransfer(charUuid, itemUuid, containerUuid, update, slot);
            }

        } else {
            if (update.isEquipped() != null) {
            slot.setEquipped(update.isEquipped());
            }
            if (update.isAttuned() != null) {
                slot.setAttuned(update.isAttuned());
            }
            if (update.getQuantity() != null) {
                if(update.getQuantity() < 0) {
                    return null;
                }
                if(!updateContainerCurrentCapacity(charUuid, itemUuid, containerUuid, update, slot)) {
                    return null;
                }

                slot.setQuantity(update.getQuantity());
            }
            if(update.getInAttackTab() != null && slot.isInAttackTab() != null) {
                slot.setInAttackTab(update.getInAttackTab());
            }
            
            return repo.save(slot);
        }
    }

    @Transactional
    private boolean updateContainerCurrentCapacity(UUID charUuid, UUID itemUuid, UUID containerUuid, CharacterHasItemUpdate update, CharacterHasItemSlot slot) {

        Optional<Container> optionalContainer =  containerRepo.findById(new ContainerId(containerUuid, charUuid));        
        ItemCatalog item = itemCatalogService.getItemWithUUID(itemUuid);

        if(optionalContainer.isPresent() && item != null) {
            Container container = optionalContainer.get();
            
            int quantityDiff = update.getQuantity() - slot.getQuantity();
            int weightDiff = quantityDiff * item.getItemWeight();
            int newCapacity = container.getCurrentCapacity() + weightDiff;

            if(newCapacity >= 0 && newCapacity <= container.getMaxCapacity()) {
                containerRepo.updateCurrentCapacity(charUuid, containerUuid, newCapacity);
                return true;
            }
        } 
        return false;
    }

    /**
     * Handles partial container transfer where only some items are moved.
     * Creates a new slot in the target container and reduces quantity in the source.
     *
     * @param charUuid the unique identifier of the character
     * @param itemUuid the unique identifier of the item being transferred
     * @param containerUuid the source container UUID
     * @param update the update containing transfer details
     * @param currentSlot the current item slot being transferred from
     * @return the new CharacterHasItemSlot in the target container, or null if transfer failed
     */
    @Transactional 
    private CharacterHasItemSlot partialContainerTransfer(UUID charUuid, UUID itemUuid, UUID containerUuid, CharacterHasItemUpdate update, CharacterHasItemSlot currentSlot) {
        
        
        if(!checkIfItemFitsInContainerAndUpdateContainer(charUuid, update.getContainerUuid(), update) ||  
        !removeItemFromContainer(charUuid, containerUuid, update)) {
            return null;
        } 

        currentSlot.setQuantity(currentSlot.getQuantity() - update.getQuantity());
        repo.save(currentSlot);

        CharacterHasItemSlot newSlot = new CharacterHasItemSlot(itemUuid, charUuid, update.getContainerUuid(), update.getQuantity(), update.isEquipped(), update.isAttuned(), false);

        return repo.save(newSlot);
    }

    /**
     * Handles complete container transfer where all items are moved.
     * Either merges with existing slot in target container or creates new slot.
     *
     * @param charUuid the unique identifier of the character
     * @param itemUuid the unique identifier of the item being transferred
     * @param containerUuid the source container UUID
     * @param update the update containing transfer details
     * @param currentSlot the current item slot being transferred
     * @return the updated or new CharacterHasItemSlot, or null if transfer failed
     */
    @Transactional
    private CharacterHasItemSlot completeContainerTransfer(UUID charUuid, UUID itemUuid, UUID containerUuid, CharacterHasItemUpdate update, CharacterHasItemSlot currentSlot) {
        
        if (update.isEquipped() != null) {
            currentSlot.setEquipped(update.isEquipped());
        }
        if (update.isAttuned() != null) {
            currentSlot.setAttuned(update.isAttuned());
        }
        if (update.getQuantity() != null) {
            currentSlot.setQuantity(update.getQuantity());
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
                targetSlot.setQuantity(targetSlot.getQuantity() + currentSlot.getQuantity());
                repo.delete(currentSlot);
                return repo.save(targetSlot);

            } else {
                CharacterHasItemSlot newSlot = new CharacterHasItemSlot();
                newSlot.setId(new CharacterHasItemSlotId(itemUuid, charUuid, newContainerUuid));
                newSlot.setQuantity(currentSlot.getQuantity());
                newSlot.setEquipped(currentSlot.isEquipped());
                newSlot.setAttuned(currentSlot.isAttuned());
                newSlot.setInAttackTab(currentSlot.isInAttackTab());
                
                repo.delete(currentSlot);  
                return repo.save(newSlot);  
            }
        }
            
        return repo.save(currentSlot);
    }

    
       
/**
     * Checks if items can fit in a container and updates the container's capacity.
     * Validates against container weight/capacity limits and updates current usage.
     *
     * @param charUuid the unique identifier of the character
     * @param conatinerUuid the unique identifier of the target container
     * @param update the update containing item and quantity information
     * @return true if items fit and container was updated, false otherwise
     */
    @Transactional
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

    /**
     * Removes items from a container and updates the container's current capacity.
     * Validates that the container has sufficient items to remove.
     *
     * @param charUuid the unique identifier of the character
     * @param conatinerUuid the unique identifier of the source container
     * @param update the update containing item and quantity information
     * @return true if items were removed and container updated, false otherwise
     */
    @Transactional
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
