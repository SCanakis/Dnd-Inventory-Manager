package com.scanakispersonalprojects.dndapp.service.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.Container;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerId;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerView;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ContainerRepo;
import com.scanakispersonalprojects.dndapp.persistance.inventory.InventoryRepo;

import jakarta.transaction.Transactional;


/**
 * Service class for managing character inventory containers.
 * Handles business logic for creating, retrieving, updating, and deleting
 * containers, including validation and capacity management.
 * 
 * Provides complete container views that include both container metadata
 * and their current contents for display purposes.
 */
@Service
public class ContainerService {

    /** Repository for container database operations */
    private ContainerRepo containerRepo;

    /** Repository for inventory item operations */
    private InventoryRepo inventoryRepo;

    private ItemCatalogService itemCatalogService;
    /** 
     * Special UUID representing the default inventory container.
     * This container cannot be deleted or modified and serves as the
     * base inventory space for all characters.
     */
    private final static UUID inventoryContainerUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

    /**
     * Constructs a new ContainerService with the required repository dependencies.
     *
     * @param containerRepo repository for container operations
     * @param inventoryRepo repository for inventory item operations
     */
    public ContainerService(ContainerRepo containerRepo, InventoryRepo inventoryRepo, ItemCatalogService itemCatalogService) {
        this.containerRepo = containerRepo;
        this.inventoryRepo = inventoryRepo;
        this.itemCatalogService = itemCatalogService;
    }

    /**
     * Retrieves all containers belonging to a character with their current contents.
     * Creates ContainerView objects that combine container metadata with
     * the items currently stored in each container.
     *
     * @param charUuid the unique identifier of the character
     * @return list of ContainerView objects with container details and contents,
     *         or null if charUuid is null or an error occurs
     */
    public List<ContainerView> getCharactersContainers(UUID charUuid) {
        if(charUuid == null) {
            return null;
        }
        try {
            List<Container> list = containerRepo.getCharactersContainers(charUuid);
        
            List<ContainerView> result = new ArrayList<>();
            
            for(Container container: list) {
                ContainerView view = new ContainerView();
                view.setContainer(container);

                if(container.getItemUuid() != null && container.getItemUuid() != inventoryContainerUuid) {
                    view.setName(itemCatalogService.getItemWithUUID(container.getItemUuid()).getItemName());
                }

                List<CharacterHasItemProjection> items = inventoryRepo.getItemsForAContainer(charUuid, container.getContainerUuid());

                if(!items.isEmpty()) {
                    view.setItems(items);
                }
                result.add(view);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a new container for a character.
     * Validates that the container has required properties and is not using
     * the reserved inventory container UUID.
     *
     * @param charUuid the unique identifier of the character who will own the container
     * @param container the container entity to create
     * @return the saved Container entity if creation successful, null otherwise
     */
    @Transactional
    public Container createContainer(UUID charUuid, Container container) {
        if(charUuid == null || container == null || container.getItemUuid() == null || container.getMaxCapacity() <= 0 || container.getContainerUuid() == inventoryContainerUuid) {
            return null;
        }
    
        container.setCharUuid(charUuid);

        return containerRepo.save(container);
    }

    /**
     * Deletes a container if it is empty and not the special inventory container.
     * The default inventory container (UUID all zeros) cannot be deleted.
     * Only empty containers can be deleted to prevent item loss.
     *
     * @param charUuid the unique identifier of the character who owns the container
     * @param containerUuid the unique identifier of the container to delete
     * @return true if deletion was successful, false if container is not empty,
     *         doesn't exist, is the inventory container, or an error occurs
     */
    @Transactional
    public boolean deleteContainer(UUID charUuid, UUID containerUuid) {
        try {
            if(containerUuid == inventoryContainerUuid) {
                return false;
            }
            Optional<Container> optionalContainer = containerRepo.findById(new ContainerId(containerUuid, charUuid));
            if(optionalContainer.isPresent()) {
                List<CharacterHasItemProjection> items = inventoryRepo.getItemsForAContainer(charUuid, containerUuid);
                if(items.isEmpty()) {
                    containerRepo.deleteById(new ContainerId(containerUuid, charUuid));
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates the maximum capacity of a container.
     * Validates that the new maximum capacity is not less than the current
     * number of items stored in the container.
     *
     * @param charUuid the unique identifier of the character who owns the container
     * @param containerUuid the unique identifier of the container to update
     * @param maxCapacity the new maximum capacity to set
     * @return the updated Container entity if successful, null if validation fails,
     *         container doesn't exist, or new capacity is less than current usage
     */
    @Transactional
    public Container updateMaxCapacityOfContainer(UUID charUuid, UUID containerUuid, int maxCapacity) {
        if(charUuid == null || containerUuid == null) {
            return null;
        }
        
        Optional<Container> currentContainerOptional = containerRepo.findById(new ContainerId(containerUuid, charUuid));
        if(currentContainerOptional.isPresent()) {
            Container currentContainer = currentContainerOptional.get();
            if(maxCapacity < currentContainer.getCurrentCapacity()) {
                return null;
            }
            currentContainer.setMaxCapacity(maxCapacity);
            return containerRepo.save(currentContainer);
        }
        
        return null;
    }    


    @Transactional
    public boolean createInventory(UUID charUuid, int maxCapacity) {
        if(charUuid == null) {
            return false;
        }
    
        Container container = new Container(inventoryContainerUuid, charUuid, null, maxCapacity,0);
        
        containerRepo.save(container);

        return true;

    }
}
