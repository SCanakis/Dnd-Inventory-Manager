package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DndClassUnitTest {
    

    private final UUID CLASS_UUID = UUID.randomUUID();
    private final String NAME = "testClass";
    private final HitDiceValue HIT_DICE = HitDiceValue.D12;
    private final String DESC = "testDesc";
    
    @Test
    public void constructor1() {
        DndClass c = new DndClass();

        assertNull(c.getClassUuid());
        assertNull(c.getName());
        assertNull(c.getHitDiceValue());
        assertNull(c.getDescription());
    }

    @Test
    public void constructor2() {
        DndClass c = new DndClass(NAME, HIT_DICE);

        assertNull(c.getClassUuid());
        assertEquals(c.getName(), NAME);
        assertEquals(c.getHitDiceValue(), HIT_DICE);
        assertNull(c.getDescription());
    }

    @Test
    public void constructor3() {
        DndClass c = new DndClass(CLASS_UUID, NAME, HIT_DICE, DESC);

        assertEquals(c.getClassUuid(), CLASS_UUID);
        assertEquals(c.getName(), NAME);
        assertEquals(c.getHitDiceValue(), HIT_DICE);
        assertEquals(c.getDescription(), DESC);
    }

    @Test
    public void setters() {
        DndClass c = new DndClass();
        c.setClassUuid(CLASS_UUID);
        c.setName(NAME);
        c.setHitDiceValue(HIT_DICE);
        c.setDescription(DESC);

        assertEquals(c.getClassUuid(), CLASS_UUID);
        assertEquals(c.getName(), NAME);
        assertEquals(c.getHitDiceValue(), HIT_DICE);
        assertEquals(c.getDescription(), DESC);
    }
    @Test
    public void equals() {
        DndClass c1 = new DndClass(CLASS_UUID, NAME, HIT_DICE, DESC);
        DndClass c2 = new DndClass();
        c2.setClassUuid(CLASS_UUID);
        assertEquals(c1, c2);
    }



    
    
}
