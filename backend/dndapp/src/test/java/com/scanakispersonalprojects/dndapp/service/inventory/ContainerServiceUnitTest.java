package com.scanakispersonalprojects.dndapp.service.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.scanakispersonalprojects.dndapp.model.inventory.containers.Container;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerView;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class ContainerServiceUnitTest {
    
    @Autowired
    ContainerService containerService;


    private final UUID thorinUuid = UUID.fromString("eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9");
    private final UUID bagOfHoldingUuid = UUID.fromString("aaaa0000-0000-0000-0000-000000000012");
    private final UUID beltPouchUuid = UUID.fromString("cccc0000-0000-0000-0000-000000000001");
    private final UUID inventoryContainerUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");


    @Test
    public void getCharactersContainers_returnsContainers() throws Exception{
        List<ContainerView> result = containerService.getCharactersContainers(thorinUuid);
        assertTrue(!result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    public void getCharactersContainers_returnsNull() throws Exception {
        List<ContainerView> result = containerService.getCharactersContainers(null);
        assertNull(result);
        result = containerService.getCharactersContainers(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    public void createContainer_succesfull() throws Exception {
        Container container = containerService.createContainer(thorinUuid, new Container(UUID.fromString("00000000-0000-0000-0000-000000000002"), thorinUuid, bagOfHoldingUuid, 500, 0));

        assertEquals(thorinUuid, container.getCharUuid());
        assertEquals(bagOfHoldingUuid, container.getItemUuid());
        assertEquals(500, container.getMaxCapacity());

        List<ContainerView> result = containerService.getCharactersContainers(thorinUuid);
        assertEquals(bagOfHoldingUuid, result.get(2).getContainer().getItemUuid());
    }

    @Test
    public void createContainer_null() throws Exception {
        Container container = containerService.createContainer(null, new Container(null, null, null, -500, 0));

        assertNull(container);
    }

    @Test
    public void createContainer_succesfullNoId() throws Exception {
        Container container = containerService.createContainer(thorinUuid, new Container(null, null, bagOfHoldingUuid, 500, 0));

        assertEquals(thorinUuid, container.getCharUuid());
        assertEquals(bagOfHoldingUuid, container.getItemUuid());
        assertEquals(500, container.getMaxCapacity());
    }

    @Test
    public void deleteContainer_successfull() throws Exception {
        Container container = containerService.createContainer(thorinUuid, new Container(null, thorinUuid, bagOfHoldingUuid, 500, 0));
        System.out.println("containerUUID: " + container.getContainerUuid());

        List<ContainerView> containers = containerService.getCharactersContainers(thorinUuid);
        
        assertEquals(3, containers.size());


        boolean result = containerService.deleteContainer(thorinUuid, container.getContainerUuid());
        assertTrue(result);

        containers = containerService.getCharactersContainers(thorinUuid);
        assertEquals(2, containers.size());
    }

    @Test
    public void deleteContainer_inventory() throws Exception {
        boolean result = containerService.deleteContainer(thorinUuid, inventoryContainerUuid);
        assertFalse(result);
    }

    @Test void deteleNonEmptyContainer() throws Exception {
        boolean result = containerService.deleteContainer(thorinUuid,beltPouchUuid);
        assertFalse(result);
    }
 
    @Test
    public void updateMaxCapacityOfContainer_succesful() throws Exception {
        Container container = containerService.updateMaxCapacityOfContainer(thorinUuid, beltPouchUuid, 200);

        assertEquals(200, container.getMaxCapacity());
    }
    
    @Test
    public void updateMaxCapacityOfContainer_null() throws Exception {
        Container container = containerService.updateMaxCapacityOfContainer(null, null, 200);

        assertNull(container);
    }

}
