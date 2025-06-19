package com.scanakispersonalprojects.dndapp.service.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.Container;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerId;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerView;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ContainerRepo;
import com.scanakispersonalprojects.dndapp.persistance.inventory.InventoryJPARepo;

@Service
public class ContainerService {
    

    private ContainerRepo containerRepo;
    private InventoryJPARepo inventoryJPARepo;

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

}
