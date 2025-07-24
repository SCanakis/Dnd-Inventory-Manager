package com.scanakispersonalprojects.dndapp.model.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.Container;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerId;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerView;

@SpringBootTest
public class ContainerUnitTest {
    
    private final UUID randomUuid1 = UUID.randomUUID();
    private final UUID randomUuid2 = UUID.randomUUID();
    private final UUID randomUuid3 = UUID.randomUUID();

    @Test
    public void container_constructors() {
        Container container = new Container();
        
        assertNotNull(container);
        // assertEquals(0, container.getCurrentCapacity());
        assertNull(container.getCharUuid());
        assertNull(container.getContainerUuid());
        assertNull(container.getId());
        assertNull(container.getItemUuid());
        assertEquals(0, container.getMaxCapacity());

        container = new Container(randomUuid3, randomUuid2, randomUuid1, 400, 200);

        assertEquals(randomUuid3, container.getContainerUuid());
        assertEquals(randomUuid2, container.getCharUuid());
        assertEquals(randomUuid1, container.getItemUuid());
        assertNotNull(container.getId());
        assertEquals(400, container.getMaxCapacity());
        // assertEquals(200, container.getCurrentCapacity());
    }

    @Test
    public void container_setters() {
        Container container = new Container();
        
        assertNotNull(container);
        // assertEquals(0, container.getCurrentCapacity());
        assertNull(container.getCharUuid());
        assertNull(container.getContainerUuid());
        assertNull(container.getId());
        assertNull(container.getItemUuid());
        assertEquals(0, container.getMaxCapacity());

        container.setItemUuid(randomUuid1);
        container.setCharUuid(randomUuid2);
        container.setContainerUuid(randomUuid3);
        container.setMaxCapacity(400);
        container.setCurrentCapacity(200);


        assertEquals(randomUuid3, container.getContainerUuid());
        assertEquals(randomUuid2, container.getCharUuid());
        assertEquals(randomUuid1, container.getItemUuid());
        assertNotNull(container.getId());
        assertEquals(400, container.getMaxCapacity());
        // assertEquals(200, container.getCurrentCapacity());
    }

    @Test
    public void containerId_equals() {
        ContainerId containerId1 = new ContainerId(randomUuid1, randomUuid2);
        ContainerId containerId2 = new ContainerId(randomUuid1, randomUuid2);
        assertTrue(containerId1.equals(containerId2));
    }

    @Test
    public void containerView_constructor() {
        List<CharacterHasItemProjection> items = new ArrayList<>();
        Container container = new Container(randomUuid3, randomUuid2, randomUuid1, 400, 200);


        ContainerView view = new ContainerView(container, items);
        
        assertNotNull(view.getItems());
        assertNotNull(view.getContainer());
    }

    
}
