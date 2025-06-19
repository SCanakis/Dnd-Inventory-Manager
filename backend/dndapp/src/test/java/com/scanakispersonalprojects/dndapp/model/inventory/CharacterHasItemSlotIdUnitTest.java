package com.scanakispersonalprojects.dndapp.model.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlotId;

@SpringBootTest
public class CharacterHasItemSlotIdUnitTest {
    
    @Test
    void testConstructorAndGetters() {
        UUID itemUuid = UUID.randomUUID();
        UUID charUuid = UUID.randomUUID();
        UUID containerUuid = UUID.randomUUID();
        
        CharacterHasItemSlotId id = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);
        
        assertEquals(itemUuid, id.getItemUuid());
        assertEquals(charUuid, id.getCharUuid());
        assertEquals(containerUuid, id.getContainerUuid());
    }

    @Test
    void testSetters() {
        CharacterHasItemSlotId id = new CharacterHasItemSlotId();
        UUID itemUuid = UUID.randomUUID();
        UUID charUuid = UUID.randomUUID();
        UUID containerUuid = UUID.randomUUID();
        
        id.setItemUuid(itemUuid);
        id.setCharUuid(charUuid);
        id.setContainerUuid(containerUuid);
        
        assertEquals(itemUuid, id.getItemUuid());
        assertEquals(charUuid, id.getCharUuid());
        assertEquals(containerUuid, id.getContainerUuid());
    }

    @Test
    void testEqualsWhenEqual() {
        UUID itemUuid = UUID.randomUUID();
        UUID charUuid = UUID.randomUUID();
        UUID containerUuid = UUID.randomUUID();
        
        CharacterHasItemSlotId id1 = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);
        CharacterHasItemSlotId id2 = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);
        
        assertTrue(id1.equals(id2));
        assertTrue(id2.equals(id1));
    }

    @Test
    void testEqualsWhenNotEqual() {
        UUID itemUuid1 = UUID.randomUUID();
        UUID itemUuid2 = UUID.randomUUID();
        UUID charUuid = UUID.randomUUID();
        UUID containerUuid = UUID.randomUUID();
        
        CharacterHasItemSlotId id1 = new CharacterHasItemSlotId(itemUuid1, charUuid, containerUuid);
        CharacterHasItemSlotId id2 = new CharacterHasItemSlotId(itemUuid2, charUuid, containerUuid);
        
        assertFalse(id1.equals(id2));
        assertFalse(id1.equals(null));
    }

    @Test
    void testHashCode() {
        UUID itemUuid = UUID.randomUUID();
        UUID charUuid = UUID.randomUUID();
        UUID containerUuid = UUID.randomUUID();
        
        CharacterHasItemSlotId id1 = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);
        CharacterHasItemSlotId id2 = new CharacterHasItemSlotId(itemUuid, charUuid, containerUuid);
        
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testWithNullValues() {
        CharacterHasItemSlotId id1 = new CharacterHasItemSlotId(null, null, null);
        CharacterHasItemSlotId id2 = new CharacterHasItemSlotId(null, null, null);
        
        assertTrue(id1.equals(id2));
        assertEquals(id1.hashCode(), id2.hashCode());
    }

}
