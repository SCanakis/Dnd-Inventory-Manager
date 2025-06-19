package com.scanakispersonalprojects.dndapp.model.inventory;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlotId;

@SpringBootTest
public class CharacterHasItemSlotUnitTest {
    
    @Test
    void testDefaultConstructor() {
        CharacterHasItemSlot slot = new CharacterHasItemSlot();
        
        assertNull(slot.getId());
        assertEquals(1, slot.getQuantity()); // default value
        assertFalse(slot.isEquipped());
        assertFalse(slot.isAttuned());
        assertFalse(slot.isInAttackTab());
    }

    @Test
    void testParameterizedConstructor() {
        UUID itemUuid = UUID.randomUUID();
        UUID charUuid = UUID.randomUUID();
        UUID slotUuid = UUID.randomUUID();
        
        CharacterHasItemSlot slot = new CharacterHasItemSlot(
            itemUuid, charUuid, slotUuid, 5, true, true, false
        );
        
        assertNotNull(slot.getId());
        assertEquals(5, slot.getQuantity());
        assertTrue(slot.isEquipped());
        assertTrue(slot.isAttuned());
        assertFalse(slot.isInAttackTab());
    }

    @Test
    void testIdGetterAndSetter() {
        CharacterHasItemSlot slot = new CharacterHasItemSlot();
        CharacterHasItemSlotId id = new CharacterHasItemSlotId(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );
        
        slot.setId(id);
        assertEquals(id, slot.getId());
    }

    @Test
    void testQuantityGetterAndSetter() {
        CharacterHasItemSlot slot = new CharacterHasItemSlot();
        
        slot.setQuantity(10);
        assertEquals(10, slot.getQuantity());
        
        slot.setQuantity(0);
        assertEquals(0, slot.getQuantity());
    }

    @Test
    void testBooleanGettersAndSetters() {
        CharacterHasItemSlot slot = new CharacterHasItemSlot();
        
        slot.setEquipped(true);
        assertTrue(slot.isEquipped());
        
        slot.setAttuned(true);
        assertTrue(slot.isAttuned());
        
        slot.setInAttackTab(true);
        assertTrue(slot.isInAttackTab());
        
        // Test setting back to false
        slot.setEquipped(false);
        slot.setAttuned(false);
        slot.setInAttackTab(false);
        
        assertFalse(slot.isEquipped());
        assertFalse(slot.isAttuned());
        assertFalse(slot.isInAttackTab());
    }

    @Test
    void testAllSettersAndGetters() {
        CharacterHasItemSlot slot = new CharacterHasItemSlot();
        CharacterHasItemSlotId id = new CharacterHasItemSlotId(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );
        
        slot.setId(id);
        slot.setQuantity(3);
        slot.setEquipped(true);
        slot.setAttuned(false);
        slot.setInAttackTab(true);
        
        assertEquals(id, slot.getId());
        assertEquals(3, slot.getQuantity());
        assertTrue(slot.isEquipped());
        assertFalse(slot.isAttuned());
        assertTrue(slot.isInAttackTab());
    }

}
