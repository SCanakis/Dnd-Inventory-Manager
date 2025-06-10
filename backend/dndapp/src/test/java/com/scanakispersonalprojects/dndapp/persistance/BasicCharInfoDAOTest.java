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
import org.testcontainers.junit.jupiter.Testcontainers;

import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
class BasicCharInfoRepositoryIntegrationTest  {

    @Autowired
    private CharacterInfoDao dao;

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
    public void testGetCharInfo_Success() {
        // Act
        CharacterBasicInfoView result = dao.getCharInfo(testCharUuid);

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
        assertEquals(result.abilityScores().get(AbilityScore.STRENGTH), 15);
        assertEquals(result.abilityScores().get(AbilityScore.DEXTERITY), 14);

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
    public void testGetCharInfo_CharacterNotFound() {
        // Act
        CharacterBasicInfoView result = dao.getCharInfo(UUID.randomUUID());

        // Assert
        assertEquals(null,result);
    }

    @Test
    public void testUpdateCurrentHealth() {
        // Arrange
        int updatedHp = 40;
        
        // Act
        dao.updateCurrentHealth(testCharUuid,updatedHp);
        
        // Assert
        assertEquals(dao.getCharInfo(testCharUuid).hpHandler().currentHp(), updatedHp);

    }

    @Test
    public void testUpdateTempHealth() {
        // Arrange
        int tempHp = 40;

        // Act
        dao.updateTempHealth(testCharUuid,tempHp);
        
        // Assert
        assertEquals(dao.getCharInfo(testCharUuid).hpHandler().tempHp(), tempHp);
    }


    @Test
    public void testUpdateHitDice() {
        // Arrange
        int newHitDice = 5;

        // Act
        dao.updateHitDice(testCharUuid,testClassUuid, newHitDice);
        
        // Assert
        assertEquals(dao.getCharInfo(testCharUuid).classes().get(0).currentHitDice(), newHitDice);
    }

    @Test
    public void updateName() {
        // Arange
        String updatedName = "Updated Test Character";

        // Act
        dao.updateName(testCharUuid, updatedName);
        
        // Assert
        assertEquals(dao.getCharInfo(testCharUuid).name(), updatedName);
    }

    @Test
    public void updatedSuccessfulST() {
        // Arange
        int updatedSuccesses = 3;
        
        // Act
        dao.updateSuccessST(testCharUuid, updatedSuccesses);
        
        // Assert
        assertEquals(dao.getCharInfo(testCharUuid).deathSavingThrowsHelper().success(), updatedSuccesses);
    }

    @Test
    public void updatedFailureST() {
        // Arange
        int updatedFailure = 0;
        
        // Act
        dao.updateFailureST(testCharUuid, updatedFailure);
        
        // Assert
        assertEquals(dao.getCharInfo(testCharUuid).deathSavingThrowsHelper().failure(), updatedFailure);
    }

    @Test
    public void updateAbilityScore() {
        // Arange
        int asValue = 29;
        
        // Act
        dao.updateAbilityScore(testCharUuid, asValue, AbilityScore.CHARISMA);
        
        // Assert
        assertEquals(dao.getCharInfo(testCharUuid).abilityScores().get(AbilityScore.CHARISMA) , asValue);
    }


}
