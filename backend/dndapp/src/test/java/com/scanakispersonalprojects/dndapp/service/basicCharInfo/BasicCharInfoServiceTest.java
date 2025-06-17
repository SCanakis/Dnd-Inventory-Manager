package com.scanakispersonalprojects.dndapp.service.basicCharInfo;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClassDetail;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfoUpdateDTO;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.HPHandler;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.HitDiceValue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class BasicCharInfoServiceTest {
    
    @Autowired
    private CharacterInfoService service;

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
        INSERT INTO race (race_uuid, name, stat_increases)
        VALUES (?, ?, ?::json)
        """,
        testRaceUuid, 
        "aTestRace", 
        "{\"strength\": 1, \"dexterity\": 1, \"constitution\": 1, \"intelligence\": 0, \"wisdom\": 0, \"charisma\": 0}"
    );

        // Insert test background
        jdbcTemplate.update("""
            INSERT INTO background (background_uuid, name, description, starting_gold)
            VALUES (?, ?, ?, ?)
            """,
            testBackgroundUuid, "aTestBackground", "Served in a temple", 15
        );

        // Insert test class
        jdbcTemplate.update("""
            INSERT INTO class (class_uuid, name, hit_dice_value, description)
            VALUES (?, ?, ?, ?)
            """,
            testClassUuid, "aTestClass", "D10", "A master of martial combat"
        );

        // Insert test subclass
        jdbcTemplate.update("""
            INSERT INTO subclass (subclass_uuid, name, class_source)
            VALUES (?, ?, ?)
            """,
            testSubclassUuid, "aTestSubClass", testClassUuid
        );

        // Insert aTestCharacter info
        jdbcTemplate.update("""
                INSERT INTO characters_info (char_info_uuid, name, inspiration, race_uuid, background_uuid, ability_scores, hp_handler, death_saving_throws)
                VALUES (?, ?, ?, ?, ?, ?::json, ?::json, ?::json)
                """,
                testCharUuid,
                "aTestCharacter",
                false,
                testRaceUuid,
                testBackgroundUuid,
                "{\"strength\": 16, \"dexterity\": 12, \"constitution\": 15, \"intelligence\": 10, \"wisdom\": 13, \"charisma\": 8}",
                "{\"maxHp\": 12, \"currentHp\": 12, \"temporaryHp\": 0}",
                "{\"successes\": 0, \"failures\": 0}"
        );

        // Insert character class
        jdbcTemplate.update("""
            INSERT INTO character_class (char_info_uuid, class_uuid, subclass_uuid, level, hit_dice_remaining)
            VALUES (?, ?, ?, ?, ?)
            """,
            testCharUuid, testClassUuid, testSubclassUuid, 5, 4
        );

        // Insert test user
        UUID testUserUuid = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO users (user_uuid, username, password, enabled)
            VALUES (?, ?, ?, ?)
            """,
            testUserUuid, "aTestUser", "{noop}password123", true
        );

        // Grant ROLE_USER authority
        jdbcTemplate.update("""
            INSERT INTO authorities (username, authority)
            VALUES (?, ?)
            """,
            "aTestUser", "ROLE_USER"
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
        CharacterBasicInfoView result = service.getCharacterBasicInfoView(testCharUuid);
        // Assert
        assertNotNull(result);
        assertEquals(result.charInfoUUID(), testCharUuid);
        assertEquals(result.name(), "aTestCharacter");
        assertEquals(result.race(), "aTestRace");
        assertEquals(result.background(), "aTestBackground");
        assertEquals(result.inspiration(), false);
        assertEquals(result.raceUUID(), testRaceUuid);
        assertEquals(result.backgroundUUID(), testBackgroundUuid);

        // Verify ability scores
        assertEquals((int) result.abilityScores().get(AbilityScore.strength), 16);
        assertEquals((int) result.abilityScores().get(AbilityScore.dexterity), 12);

        // Verify classes
        assertEquals(result.classes().isEmpty(), false);
        assertEquals(result.classes().size(), 1);
        assertEquals(result.classes().get(0).className(), "aTestClass");
        assertEquals(result.classes().get(0).level(),(short) 5);
        assertEquals(result.classes().get(0).hitDiceRemaining(), (short) 4);
        assertEquals(result.classes().get(0).hitDiceValue(), HitDiceValue.D10);

        // Verify HP handler
        assertNotNull(result.hpHandler());
        assertEquals(result.hpHandler().currentHp(), 12);
        assertEquals(result.hpHandler().maxHp(), 12);
        assertEquals(result.hpHandler().temporaryHp(), 0);

        // Verify death saving throws
        assertNotNull(result.deathSavingThrowsHelper());
        assertEquals(result.deathSavingThrowsHelper().successes(), 0);
        assertEquals(result.deathSavingThrowsHelper().failures(), 0);
    }

    @Test
    public void updateCharInfoTest() throws Exception {

        CharacterInfoUpdateDTO characterInfo = new CharacterInfoUpdateDTO();

        CharacterClassDetail classDetail = new CharacterClassDetail(testClassUuid, null, null, testSubclassUuid, null, (short)20, (short)19);

        characterInfo.setName("Updated aTestCharacter");
        characterInfo.setInspiration((Boolean) false);
        characterInfo.setAbilityScores(
            new HashMap<AbilityScore, Integer>() {{
                put(AbilityScore.strength, 20);
                put(AbilityScore.dexterity, 20);
                put(AbilityScore.constitution, 20);
                put(AbilityScore.wisdom, 20);
                put(AbilityScore.intelligence, 20);
                put(AbilityScore.charisma, 20);
            }}
        );
        characterInfo.setHpHandler(
            new HPHandler(100,100,100)
        );
        characterInfo.setDeathSavingThrowsHelper(new DeathSavingThrowsHelper(3,3));
        characterInfo.setCharacterClassDetail(new ArrayList<>() {{
            add(classDetail);
            }});
     
        // Act
        CharacterBasicInfoView result = service.updateUsingPatch(testCharUuid, characterInfo);

        // Assert
        assertNotNull(result);
        assertEquals(result.charInfoUUID(), testCharUuid);

        assertEquals(result.name(), "Updated aTestCharacter");
        assertEquals(result.name(), characterInfo.getName());

        assertEquals(result.race(), "aTestRace");
        assertEquals(result.background(), "aTestBackground");

        assertEquals(result.inspiration(), false);
        assertEquals(result.inspiration(), characterInfo.getInspiration());

        assertEquals(result.raceUUID(), testRaceUuid);
        assertEquals(result.backgroundUUID(), testBackgroundUuid);

        // Verify ability scores
        assertEquals((int) result.abilityScores().get(AbilityScore.strength), 20);
        assertEquals((int) result.abilityScores().get(AbilityScore.dexterity), 20);
        assertEquals((int)result.abilityScores().get(AbilityScore.strength), characterInfo.getAbilityScores().get(AbilityScore.strength));

        // Verify classes
        assertEquals(result.classes().isEmpty(), false);
        assertEquals(result.classes().size(), 1);
        assertEquals(result.classes().get(0).className(), "aTestClass");
        assertEquals(result.classes().get(0).level(), (short) 20);

        assertEquals(result.classes().get(0).hitDiceRemaining(),(short) 19);
        assertEquals(result.classes().get(0).hitDiceRemaining(), characterInfo.getCharacterClassDetail().get(0).hitDiceRemaining());
        
        assertEquals(result.classes().get(0).hitDiceValue(), HitDiceValue.D10);

        // Verify HP handler
        assertNotNull(result.hpHandler());
        
        assertEquals(result.hpHandler().currentHp(), 100);
        assertEquals(result.hpHandler().currentHp(), characterInfo.getHpHandler().currentHp());

        assertEquals(result.hpHandler().maxHp(), 100);
        assertEquals(result.hpHandler().temporaryHp(), 100);

        // Verify death saving throws
        assertNotNull(result.deathSavingThrowsHelper());

        assertEquals(result.deathSavingThrowsHelper().successes(), 3);
        assertEquals(result.deathSavingThrowsHelper().successes(), characterInfo.getDeathSavingThrowsHelper().successes());
        
        assertEquals(result.deathSavingThrowsHelper().failures(), 3);
        assertEquals(result.deathSavingThrowsHelper().failures(), characterInfo.getDeathSavingThrowsHelper().failures());

    }

    @Test
    public void updateEmptyCharViewPatch() throws Exception {
        // Arrange

        CharacterInfoUpdateDTO patch = new CharacterInfoUpdateDTO();

        // Act
        CharacterBasicInfoView result = service.updateCharInfo(testCharUuid, patch);

        assertNotNull(result);
        assertEquals(result.charInfoUUID(), testCharUuid);
        assertEquals(result.name(), "aTestCharacter");
        assertEquals(result.race(), "aTestRace");
        assertEquals(result.background(), "aTestBackground");
        assertEquals(result.inspiration(), false);
        assertEquals(result.raceUUID(), testRaceUuid);
        assertEquals(result.backgroundUUID(), testBackgroundUuid);

        // Verify ability scores
        assertEquals((int) result.abilityScores().get(AbilityScore.strength), 16);
        assertEquals((int) result.abilityScores().get(AbilityScore.dexterity), 12);

        // Verify classes
        assertEquals(result.classes().isEmpty(), false);
        assertEquals(result.classes().size(), 1);
        assertEquals(result.classes().get(0).className(), "aTestClass");
        assertEquals(result.classes().get(0).level(), (short)5);
        assertEquals(result.classes().get(0).hitDiceRemaining(), (short) 4);
        assertEquals(result.classes().get(0).hitDiceValue(), HitDiceValue.D10);

        // Verify HP handler
        assertNotNull(result.hpHandler());
        assertEquals(result.hpHandler().currentHp(), 12);
        assertEquals(result.hpHandler().maxHp(), 12);
        assertEquals(result.hpHandler().temporaryHp(), 0);

        // Verify death saving throws
        assertNotNull(result.deathSavingThrowsHelper());
        assertEquals(result.deathSavingThrowsHelper().successes(), 0);
        assertEquals(result.deathSavingThrowsHelper().failures(), 0);
    }


}
