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
import com.scanakispersonalprojects.dndapp.persistance.inventory.InventoryJPARepo;

import jakarta.transaction.Transactional;

@Service
public class ContainerService {

    

    private ContainerRepo containerRepo;
    private InventoryJPARepo inventoryJPARepo;
    private final static UUID inventoryContainerUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");


    public ContainerService(ContainerRepo containerRepo, InventoryJPARepo inventoryJPARepo) {
        this.containerRepo = containerRepo;
        this.inventoryJPARepo = inventoryJPARepo;
    }

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
                List<CharacterHasItemProjection>  items = inventoryJPARepo.getItemsForAContainer(charUuid, container.getContainerUuid());

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

    @Transactional
    public Container createContainer(UUID charUuid, Container container) {
        if(charUuid == null || container == null || container.getItemUuid() == null || container.getMaxCapacity() <= 0) {
            return null;
        }
        
        if(container.getId() != null && container.getContainerUuid() != null) {
            container.setContainerUuid(charUuid);
        }
        if(container.getId() == null) {
            container.setId(new ContainerId());
        }

        container.setCharUuid(charUuid);

        return containerRepo.save(container);
    }

    @Transactional
    public boolean deleteContainer(UUID charUuid, UUID containerUuid) {
        try {
            if(containerUuid == inventoryContainerUuid) {
                return false;
            }
            Optional<Container> optionalContainer = containerRepo.findById(new ContainerId(containerUuid, charUuid));
            if(optionalContainer.isPresent()) {
                List<CharacterHasItemProjection> items = inventoryJPARepo.getItemsForAContainer(charUuid, optionalContainer.get().getContainerUuid());
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

    @Transactional
    public Container updateMaxCapacityOfContainer(UUID charUuid, UUID containerUuid, int maxCapacity) {
        if(charUuid == null || containerUuid == null) {
            return null;
        }
        
        Optional<Container> currentContainerOptional = containerRepo.findById(new ContainerId(containerUuid, charUuid));
        if(currentContainerOptional.isPresent()) {
            Container currentContainer = currentContainerOptional.get();
            currentContainer.setMaxCapacity(maxCapacity);
            return containerRepo.save(currentContainer);
        }
        
        return null;
    }    

}
