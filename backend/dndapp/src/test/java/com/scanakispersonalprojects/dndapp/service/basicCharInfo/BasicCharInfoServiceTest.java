package com.scanakispersonalprojects.dndapp.service.basicCharInfo;


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
import org.testcontainers.junit.jupiter.Testcontainers;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
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
        jdbcTemplate.update("""
        INSERT INTO race (race_uuid, name, stat_increase_str, stat_increase_dex, stat_increase_con,
                          stat_increase_int, stat_increase_wis, stat_increase_cha)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """,
        testRaceUuid, "Human", 1, 1, 1, 0, 0, 0
    );

        // Insert test background
        jdbcTemplate.update("""
            INSERT INTO background (background_uuid, name, description, starting_gold)
            VALUES (?, ?, ?, ?)
            """,
            testBackgroundUuid, "Acolyte", "Served in a temple", 15
        );

        // Insert test class
        jdbcTemplate.update("""
            INSERT INTO class (class_uuid, name, hit_dice_value, description)
            VALUES (?, ?, ?, ?)
            """,
            testClassUuid, "Fighter", 10, "A master of martial combat"
        );

        // Insert test subclass
        jdbcTemplate.update("""
            INSERT INTO subclass (subclass_uuid, name, class_source)
            VALUES (?, ?, ?)
            """,
            testSubclassUuid, "Champion", testClassUuid
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
        jdbcTemplate.update("""
            INSERT INTO character_class (char_info_uuid, class_uuid, subclass_uuid, level, hit_dice_remaining)
            VALUES (?, ?, ?, ?, ?)
            """,
            testCharUuid, testClassUuid, testSubclassUuid, 5, 4
        );

        // Insert HP handler
        jdbcTemplate.update("""
            INSERT INTO hp_handler (char_info_uuid, current_hp, max_hp, temp_hp)
            VALUES (?, ?, ?, ?)
            """,
            testCharUuid, 45, 50, 5
        );

        // Insert death saving throws
        jdbcTemplate.update("""
            INSERT INTO death_saving_throws (char_info_uuid, success, failure)
            VALUES (?, ?, ?)
            """,
            testCharUuid, 2, 1
        );

        // Insert test user
        UUID testUserUuid = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO users (user_uuid, username, password, enabled)
            VALUES (?, ?, ?, ?)
            """,
            testUserUuid, "testuser", "{noop}password123", true
        );

        // Grant ROLE_USER authority
        jdbcTemplate.update("""
            INSERT INTO authorities (username, authority)
            VALUES (?, ?)
            """,
            "testuser", "ROLE_USER"
        );

        // Link user to character
        jdbcTemplate.update("""
            INSERT INTO users_characters (user_uuid, character_uuid)
            VALUES (?, ?)
            """,
            testUserUuid, testCharUuid
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
        assertEquals((int) result.abilityScores().get(AbilityScore.strength), 15);
        assertEquals((int) result.abilityScores().get(AbilityScore.dexterity), 14);

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
        as.put(AbilityScore.strength, 29);

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
        assertEquals((int) result.abilityScores().get(AbilityScore.strength), 29);
        assertEquals((int) result.abilityScores().get(AbilityScore.dexterity), 14);
        assertEquals((int)result.abilityScores().get(AbilityScore.strength), patch.abilityScore().get(AbilityScore.strength));

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
        assertEquals((int) result.abilityScores().get(AbilityScore.strength), 15);
        assertEquals((int) result.abilityScores().get(AbilityScore.dexterity), 14);

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
