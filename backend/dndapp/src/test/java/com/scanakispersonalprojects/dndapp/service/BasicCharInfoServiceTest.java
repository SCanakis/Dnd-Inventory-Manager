package com.scanakispersonalprojects.dndapp.service;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BasicCharInfoServiceTest {
    
    @Autowired
    private CharacterService service;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private UUID testCharUuid;
    private UUID testRaceUuid;
    private UUID testBackgroundUuid;
    private UUID testClassUuid;
    private UUID testSubclassUuid;

    @BeforeEach
    void setUp() {
        testCharUuid = UUID.randomUUID();
        testRaceUuid = UUID.randomUUID();
        testBackgroundUuid = UUID.randomUUID();
        testClassUuid = UUID.randomUUID();
        testSubclassUuid = UUID.randomUUID();

        setupTestData();
    }

    private void setupTestData() {
        // Insert test race
        jdbcTemplate.update(
            "INSERT INTO race (race_uuid, name) VALUES (?, ?)",
            testRaceUuid, "Human"
        );

        // Insert test background
        jdbcTemplate.update(
            "INSERT INTO background (background_uuid, name) VALUES (?, ?)",
            testBackgroundUuid, "Acolyte"
        );

        // Insert test class
        jdbcTemplate.update(
            "INSERT INTO class (class_uuid, name, hit_dice_value) VALUES (?, ?, ?)",
            testClassUuid, "Fighter", 10
        );

        // Insert test subclass
        jdbcTemplate.update(
            "INSERT INTO subclass (subclass_uuid, name) VALUES (?, ?)",
            testSubclassUuid, "Champion"
        );

        // Insert test character info
        jdbcTemplate.update("""
            INSERT INTO characters_info 
            (char_info_uuid, name, inspiration, background_uuid, race_uuid, 
             strength, dexterity, constitution, intelligence, wisdom, charisma) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            testCharUuid, "Test Character", true, testBackgroundUuid, testRaceUuid,
            15, 14, 13, 12, 10, 8
        );

        // Insert character class
        jdbcTemplate.update(
            "INSERT INTO character_class (char_info_uuid, class_uuid, subclass_uuid, level, hit_dice_remaining) VALUES (?, ?, ?, ?, ?)",
            testCharUuid, testClassUuid, testSubclassUuid, 5, 4
        );

        // Insert HP handler
        jdbcTemplate.update(
            "INSERT INTO hp_handler (char_info_uuid, current_hp, max_hp, temp_hp) VALUES (?, ?, ?, ?)",
            testCharUuid, 45, 50, 5
        );

        // Insert death saving throws
        jdbcTemplate.update(
            "INSERT INTO death_saving_throws (char_info_uuid, success, failure) VALUES (?, ?, ?)",
            testCharUuid, 2, 1
        );
    }

    @Test
    public void getCharInfoTest() throws Exception{
        // Act
        CharacterBasicInfoView result = service.getCharInfo(testCharUuid);
        // Assert
        assertNotNull(result);
        assertEquals(result.charInfoUUID(), testCharUuid);
        assertEquals(result.name(), "Test Character");
        assertEquals(result.race(), "Human");
        assertEquals(result.background(), "Acolyte");
        assertEquals(result.inspiration(), true);
        assertEquals(result.raceUUID(), testRaceUuid);
        assertEquals(result.backgroundUUID(), testBackgroundUuid);

        // Verify ability scores
        assertEquals((int) result.abilityScores().get(AbilityScore.STRENGTH), 15);
        assertEquals((int) result.abilityScores().get(AbilityScore.DEXTERITY), 14);

        // Verify classes
        assertEquals(result.classes().isEmpty(), false);
        assertEquals(result.classes().size(), 1);
        assertEquals(result.classes().get(0).className(), "Fighter");
        assertEquals(result.classes().get(0).level(), 5);
        assertEquals(result.classes().get(0).currentHitDice(), 4);
        assertEquals(result.classes().get(0).hitDiceValue(), 10);

        // Verify HP handler
        assertNotNull(result.hpHandler());
        assertEquals(result.hpHandler().currentHp(), 45);
        assertEquals(result.hpHandler().maxHp(), 50);
        assertEquals(result.hpHandler().tempHp(), 5);

        // Verify death saving throws
        assertNotNull(result.deathSavingThrowsHelper());
        assertEquals(result.deathSavingThrowsHelper().success(), 2);
        assertEquals(result.deathSavingThrowsHelper().failure(), 1);
    }

    @Test
    public void updateCharInfoTest() throws Exception {
        // Arrange
        Map<UUID, Integer> hitDice = new HashMap<>();
        hitDice.put(testClassUuid, 2);

        Map<AbilityScore, Integer> as = new HashMap<>();
        as.put(AbilityScore.STRENGTH, 29);

        CharViewPatch patch = new CharViewPatch("Updated Test Character", 0, 50, hitDice, false, as, 3, 0);

        // Act
        CharacterBasicInfoView result = service.updateCharInfo(testCharUuid, patch);

        // Assert
        assertNotNull(result);
        assertEquals(result.charInfoUUID(), testCharUuid);

        assertEquals(result.name(), "Updated Test Character");
        assertEquals(result.name(), patch.name());

        assertEquals(result.race(), "Human");
        assertEquals(result.background(), "Acolyte");

        assertEquals(result.inspiration(), false);
        assertEquals(result.inspiration(), patch.inspiration());

        assertEquals(result.raceUUID(), testRaceUuid);
        assertEquals(result.backgroundUUID(), testBackgroundUuid);

        // Verify ability scores
        assertEquals((int) result.abilityScores().get(AbilityScore.STRENGTH), 29);
        assertEquals((int) result.abilityScores().get(AbilityScore.DEXTERITY), 14);
        assertEquals((int)result.abilityScores().get(AbilityScore.STRENGTH), patch.abilityScore().get(AbilityScore.STRENGTH));

        // Verify classes
        assertEquals(result.classes().isEmpty(), false);
        assertEquals(result.classes().size(), 1);
        assertEquals(result.classes().get(0).className(), "Fighter");
        assertEquals(result.classes().get(0).level(), 5);

        assertEquals(result.classes().get(0).currentHitDice(),2);
        assertEquals(result.classes().get(0).currentHitDice(), patch.hitDice().get(testClassUuid));
        
        assertEquals(result.classes().get(0).hitDiceValue(), 10);

        // Verify HP handler
        assertNotNull(result.hpHandler());
        
        assertEquals(result.hpHandler().currentHp(), 0);
        assertEquals(result.hpHandler().currentHp(), patch.currentHP());

        assertEquals(result.hpHandler().maxHp(), 50);
        assertEquals(result.hpHandler().tempHp(), 50);

        // Verify death saving throws
        assertNotNull(result.deathSavingThrowsHelper());

        assertEquals(result.deathSavingThrowsHelper().success(), 3);
        assertEquals(result.deathSavingThrowsHelper().success(), patch.success());
        
        assertEquals(result.deathSavingThrowsHelper().failure(), 0);
        assertEquals(result.deathSavingThrowsHelper().failure(), patch.failure());

    }

    @Test
    public void updateEmptyCharViewPatch() throws Exception {
        // Arrange

        CharViewPatch patch = new CharViewPatch();

        // Act
        CharacterBasicInfoView result = service.updateCharInfo(testCharUuid, patch);

        assertNotNull(result);
        assertEquals(result.charInfoUUID(), testCharUuid);
        assertEquals(result.name(), "Test Character");
        assertEquals(result.race(), "Human");
        assertEquals(result.background(), "Acolyte");
        assertEquals(result.inspiration(), true);
        assertEquals(result.raceUUID(), testRaceUuid);
        assertEquals(result.backgroundUUID(), testBackgroundUuid);

        // Verify ability scores
        assertEquals((int) result.abilityScores().get(AbilityScore.STRENGTH), 15);
        assertEquals((int) result.abilityScores().get(AbilityScore.DEXTERITY), 14);

        // Verify classes
        assertEquals(result.classes().isEmpty(), false);
        assertEquals(result.classes().size(), 1);
        assertEquals(result.classes().get(0).className(), "Fighter");
        assertEquals(result.classes().get(0).level(), 5);
        assertEquals(result.classes().get(0).currentHitDice(), 4);
        assertEquals(result.classes().get(0).hitDiceValue(), 10);

        // Verify HP handler
        assertNotNull(result.hpHandler());
        assertEquals(result.hpHandler().currentHp(), 45);
        assertEquals(result.hpHandler().maxHp(), 50);
        assertEquals(result.hpHandler().tempHp(), 5);

        // Verify death saving throws
        assertNotNull(result.deathSavingThrowsHelper());
        assertEquals(result.deathSavingThrowsHelper().success(), 2);
        assertEquals(result.deathSavingThrowsHelper().failure(), 1);
    }


}
