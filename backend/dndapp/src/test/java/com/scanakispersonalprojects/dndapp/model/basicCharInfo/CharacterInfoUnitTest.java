package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CharacterInfoUnitTest {
    
    private final UUID CHAR_INFO_UUID = UUID.randomUUID();
    private final String NAME = "aTestChar";
    private final boolean INSPIRATION = false;
    private final UUID RACE_UUID = UUID.randomUUID();
    private final UUID BG_UUID = UUID.randomUUID();
    private final Map<AbilityScore, Integer> AS = new HashMap<>(){{
        put(AbilityScore.strength, 20);
        put(AbilityScore.dexterity, 20);
        put(AbilityScore.constitution, 20);
        put(AbilityScore.intelligence, 20);
        put(AbilityScore.wisdom, 20);
        put(AbilityScore.charisma, 20);
    }};
    private final HPHandler HP_HANLDER = new HPHandler(100,100,100);
    private final DeathSavingThrowsHelper DS_HELPER = new DeathSavingThrowsHelper(3,3);

    @Test
    public void constructor1() {
        CharacterInfo c = new CharacterInfo();
        assertNull(c.getCharInfoUuid());
        assertNull(c.getName());
        assertFalse(c.getInspiration());
        assertNull(c.getRaceUuid());
        assertNull(c.getBackgroundUuid());
        assertNull(c.getAbilityScores());
        assertNull(c.getHpHandler());
        assertNull(c.getDeathSavingThrowsHelper());
    }

    @Test
    public void constructor2() {
        CharacterInfo c = new CharacterInfo(NAME, RACE_UUID, BG_UUID);

        assertNull(c.getCharInfoUuid());
        assertEquals(c.getName(), NAME);
        assertFalse(c.getInspiration());
        assertEquals(c.getRaceUuid(), RACE_UUID);
        assertEquals(c.getBackgroundUuid(), BG_UUID);
        assertNull(c.getAbilityScores());
        assertNull(c.getHpHandler());
        assertNull(c.getDeathSavingThrowsHelper());
    }

    @Test
    public void constructor3() {
        CharacterInfo c = new CharacterInfo(CHAR_INFO_UUID, NAME, INSPIRATION, RACE_UUID, BG_UUID, AS, HP_HANLDER, DS_HELPER);

        assertEquals(c.getCharInfoUuid(), CHAR_INFO_UUID);
        assertEquals(c.getName(), NAME);
        assertEquals(c.getInspiration(), INSPIRATION);
        assertEquals(c.getRaceUuid(), RACE_UUID);
        assertEquals(c.getBackgroundUuid(), BG_UUID);
        assertEquals(c.getAbilityScores(), AS);
        assertEquals(c.getHpHandler(), HP_HANLDER);
        assertEquals(c.getDeathSavingThrowsHelper(), DS_HELPER);
    }

    @Test
    public void setters() {
        CharacterInfo c = new CharacterInfo();
        c.setCharInfoUuid(CHAR_INFO_UUID);
        c.setName(NAME);
        c.setInspiration(INSPIRATION);
        c.setRaceUuid(RACE_UUID);
        c.setBackgroundUuid(BG_UUID);
        c.setAbilityScores(AS);
        c.setHpHandler(HP_HANLDER);
        c.setDeathSavingThrowsHelper(DS_HELPER);

        assertEquals(c.getCharInfoUuid(), CHAR_INFO_UUID);
        assertEquals(c.getName(), NAME);
        assertEquals(c.getInspiration(), INSPIRATION);
        assertEquals(c.getRaceUuid(), RACE_UUID);
        assertEquals(c.getBackgroundUuid(), BG_UUID);
        assertEquals(c.getAbilityScores(), AS);
        assertEquals(c.getHpHandler(), HP_HANLDER);
        assertEquals(c.getDeathSavingThrowsHelper(), DS_HELPER);
    }

    @Test
    public void equals() {
        CharacterInfo c1 = new CharacterInfo(CHAR_INFO_UUID, NAME, INSPIRATION, RACE_UUID, BG_UUID, AS, HP_HANLDER, DS_HELPER);
        CharacterInfo c2 = new CharacterInfo();
        c2.setCharInfoUuid(CHAR_INFO_UUID);
        assertEquals(c1, c2);

    }

}
