package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CharacterHasClassIdUnitTest {
    

    @Test
    void testConstructorAndGetters() {
        UUID charUuid = UUID.randomUUID();
        UUID classUuid = UUID.randomUUID();
        
        CharacterHasClassId id = new CharacterHasClassId(charUuid, classUuid);
        
        assertEquals(charUuid, id.getCharInfoUuid());
        assertEquals(classUuid, id.getClassUuid());
    }

    @Test
    void testSetters() {
        CharacterHasClassId id = new CharacterHasClassId();
        UUID charUuid = UUID.randomUUID();
        UUID classUuid = UUID.randomUUID();
        
        id.setCharInfoUuid(charUuid);
        id.setClassUuid(classUuid);
        
        assertEquals(charUuid, id.getCharInfoUuid());
        assertEquals(classUuid, id.getClassUuid());
    }

    @Test
    void testEqualsWhenEqual() {
        UUID charUuid = UUID.randomUUID();
        UUID classUuid = UUID.randomUUID();
        
        CharacterHasClassId id1 = new CharacterHasClassId(charUuid, classUuid);
        CharacterHasClassId id2 = new CharacterHasClassId(charUuid, classUuid);
        
        assertTrue(id1.equals(id2));
        assertTrue(id2.equals(id1));
    }

    @Test
    void testEqualsWhenNotEqual() {
        UUID charUuid1 = UUID.randomUUID();
        UUID charUuid2 = UUID.randomUUID();
        UUID classUuid = UUID.randomUUID();
        
        CharacterHasClassId id1 = new CharacterHasClassId(charUuid1, classUuid);
        CharacterHasClassId id2 = new CharacterHasClassId(charUuid2, classUuid);
        
        assertFalse(id1.equals(id2));
        assertFalse(id1.equals(null));
    }

    @Test
    void testHashCode() {
        UUID charUuid = UUID.randomUUID();
        UUID classUuid = UUID.randomUUID();
        
        CharacterHasClassId id1 = new CharacterHasClassId(charUuid, classUuid);
        CharacterHasClassId id2 = new CharacterHasClassId(charUuid, classUuid);
        
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testWithNullValues() {
        CharacterHasClassId id1 = new CharacterHasClassId(null, null);
        CharacterHasClassId id2 = new CharacterHasClassId(null, null);
        
        assertTrue(id1.equals(id2));
        assertEquals(id1.hashCode(), id2.hashCode());
    }

}
