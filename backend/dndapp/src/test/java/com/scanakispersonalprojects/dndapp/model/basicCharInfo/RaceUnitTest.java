package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class RaceUnitTest {
    private final UUID RACE_UUID = UUID.randomUUID();
    private final String NAME = "aTestBackground";
    private final Map<AbilityScore, Integer> AS = new HashMap<>() {{
        put(AbilityScore.strength, 2);
        put(AbilityScore.dexterity, 2);
        put(AbilityScore.constitution, 2);
        put(AbilityScore.intelligence, 2);
        put(AbilityScore.wisdom, 2);
        put(AbilityScore.charisma, 2);
    }};
    
    @Test
    public void constructor1() {
        Race r = new Race();
        assertNull(r.getRaceUuid());
        assertNull(r.getName());
        assertNull(r.getStatIncreases());
    }
    @Test
    public void constructor2() {
        Race r = new Race(RACE_UUID, NAME, AS);
        assertEquals(r.getRaceUuid(), RACE_UUID);
        assertEquals(r.getName(), NAME);
        assertEquals(r.getStatIncreases(), AS);
        assertEquals(r.getStatIncrease(AbilityScore.strength), 2);
    }
    @Test
    public void constructor3() {
        Race r = new Race(NAME, AS);
        assertNull(r.getRaceUuid());
        assertEquals(r.getName(), NAME);
        assertEquals(r.getStatIncreases(), AS);
        assertEquals(r.getStatIncrease(AbilityScore.strength), 2);
    }
    @Test
    public void setter() {
        Race r = new Race();
        r.setRaceUuid(RACE_UUID);
        r.setName(NAME);
        r.setStatIncreases(AS);
        assertEquals(r.getRaceUuid(), RACE_UUID);
        assertEquals(r.getName(), NAME);
        assertEquals(r.getStatIncreases(), AS);
        assertEquals(r.getStatIncrease(AbilityScore.strength), 2);
    }
    @Test
    public void equals() {
        Race r1 = new Race(RACE_UUID, NAME, AS);
        Race r2 = new Race();
        r2.setRaceUuid(RACE_UUID);
        assertEquals(r1, r2);
    }

}
