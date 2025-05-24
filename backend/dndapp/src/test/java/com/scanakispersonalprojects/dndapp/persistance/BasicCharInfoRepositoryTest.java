package com.scanakispersonalprojects.dndapp.persistance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BasicCharInfoRepositoryIntegrationTest {

    @Autowired
    private CharacterDao dao;

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
    public void testGetCharInfo_Success() {
        // Act
        CharacterBasicInfoView result = dao.getCharInfo(testCharUuid);

        // Assert
        assertNotNull(result);
        assertEquals(testCharUuid, result.charInfoUUID());
        assertEquals("Test Character", result.name());
        assertEquals("Human", result.race());
        assertEquals("Acolyte", result.background());
        assertEquals(true, result.inspiration());
        assertEquals(testRaceUuid, result.raceUUID());
        assertEquals(testBackgroundUuid, result.backgroundUUID());

        // Verify ability scores
        assertEquals(15, result.abilityScores().get(com.scanakispersonalprojects.dndapp.model.AbilityScore.STRENGTH));
        assertEquals(14, result.abilityScores().get(com.scanakispersonalprojects.dndapp.model.AbilityScore.DEXTERITY));

        // Verify classes
        assertEquals(false, result.classes().isEmpty());
        assertEquals(1, result.classes().size());
        assertEquals("Fighter", result.classes().get(0).className());
        assertEquals(5, result.classes().get(0).level());
        assertEquals(4, result.classes().get(0).currentHitDice());
        assertEquals(10, result.classes().get(0).hitDiceValue());

        // Verify HP handler
        assertNotNull(result.hpHandler());
        assertEquals(45, result.hpHandler().currentHp());
        assertEquals(50, result.hpHandler().maxHp());
        assertEquals(5, result.hpHandler().tempHp());

        // Verify death saving throws
        assertNotNull(result.deathSavingThrowsHelper());
        assertEquals(2, result.deathSavingThrowsHelper().success());
        assertEquals(1, result.deathSavingThrowsHelper().failure());
    }

    @Test
    public void testGetCharInfo_CharacterNotFound() {
        // Act
        CharacterBasicInfoView result = dao.getCharInfo(UUID.randomUUID());

        // Assert
        assertEquals(null,result);
    }
}
