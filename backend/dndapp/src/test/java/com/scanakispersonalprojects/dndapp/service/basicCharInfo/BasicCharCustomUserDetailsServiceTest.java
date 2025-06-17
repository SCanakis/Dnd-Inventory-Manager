package com.scanakispersonalprojects.dndapp.service.basicCharInfo;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClassDetail;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CustomUserPrincipal;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.HPHandler;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class BasicCharCustomUserDetailsServiceTest {
    
    @Autowired
    private CustomUserDetailsService userService;

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
        testRaceUuid, "ATestRace", "{\"strength\": 1, \"dexterity\": 1, \"constitution\": 1, \"intelligence\": 0, \"wisdom\": 0, \"charisma\": 0}"
        );

        // Insert test background
        jdbcTemplate.update("""
            INSERT INTO background (background_uuid, name, description, starting_gold)
            VALUES (?, ?, ?, ?)
            """,
            testBackgroundUuid, "ATestBackground", "Served in a temple", 15
        );

        // Insert test class
        jdbcTemplate.update("""
            INSERT INTO class (class_uuid, name, hit_dice_value, description)
            VALUES (?, ?, ?, ?)
            """,
            testClassUuid, "ATestClass", "D10", "A master of martial combat"
        );

        // Insert test subclass
        jdbcTemplate.update("""
            INSERT INTO subclass (subclass_uuid, name, class_source)
            VALUES (?, ?, ?)
            """,
            testSubclassUuid, "ATestSubclass", testClassUuid
        );

        // Insert test character info with JSON fields
        jdbcTemplate.update("""
            INSERT INTO characters_info 
            (char_info_uuid, name, inspiration, background_uuid, race_uuid,
            ability_scores, hp_handler, death_saving_throws)
            VALUES (?, ?, ?, ?, ?, ?::json, ?::json, ?::json)
            """,
            testCharUuid, "ATestCharacter", true, testBackgroundUuid, testRaceUuid,
            "{\"strength\": 15, \"dexterity\": 14, \"constitution\": 13, \"intelligence\": 12, \"wisdom\": 10, \"charisma\": 8}",
            "{\"maxHp\": 50, \"currentHp\": 45, \"temporaryHp\": 5}",
            "{\"successes\": 2, \"failures\": 1}"
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
    
    public void loadUserByUsername_returnUserPrincipal() {
        
        CustomUserPrincipal actual = (CustomUserPrincipal)userService.loadUserByUsername("testuser");
            
        // Assertions
        assertNotNull(actual);
        assertEquals("testuser", actual.getUsername());
        assertEquals("{noop}password123", actual.getPassword());
        
        // Verify characters are loaded
        assertNotNull(actual.getCharacters());
        assertEquals(1, actual.getCharacters().size());
        
        CharacterBasicInfoView actualChar = actual.getCharacters().get(0);
        
        // Test basic character info
        assertEquals(testCharUuid, actualChar.charInfoUUID());
        assertEquals("ATestCharacter", actualChar.name());
        assertTrue(actualChar.inspiration());
        assertEquals("ATestBackground", actualChar.background());
        assertEquals(testBackgroundUuid, actualChar.backgroundUUID());
        assertEquals("ATestRace", actualChar.race());
        assertEquals(testRaceUuid, actualChar.raceUUID());
        
        Map<AbilityScore, Integer> abilityScores = actualChar.abilityScores();
        assertEquals(Integer.valueOf(15), abilityScores.get(AbilityScore.strength));
        assertEquals(Integer.valueOf(14), abilityScores.get(AbilityScore.dexterity));
        assertEquals(Integer.valueOf(13), abilityScores.get(AbilityScore.constitution));
        assertEquals(Integer.valueOf(12), abilityScores.get(AbilityScore.intelligence));
        assertEquals(Integer.valueOf(10), abilityScores.get(AbilityScore.wisdom));
        assertEquals(Integer.valueOf(8), abilityScores.get(AbilityScore.charisma));
        
        List<CharacterClassDetail> classes = actualChar.classes();
        assertNotNull(classes);
        assertEquals(1, classes.size());
        
        CharacterClassDetail dndClass = classes.get(0);
        assertEquals("ATestClass", dndClass.className()); 
        assertEquals((short) 5, dndClass.level()); 

        HPHandler hpHandler = actualChar.hpHandler();
        assertNotNull(hpHandler);
        assertEquals(45, hpHandler.currentHp()); 
        assertEquals(50, hpHandler.maxHp()); 
        assertEquals(5, hpHandler.temporaryHp());

        DeathSavingThrowsHelper deathSaves = actualChar.deathSavingThrowsHelper();
        assertNotNull(deathSaves);
        assertEquals(2, deathSaves.successes());
        assertEquals(1, deathSaves.failures()); 
    }

    @Test
    public void loadUserByUsername_userNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("IDONTEXISTS");
        });
    }

    @Test
    public void getUsersUuid_userNotFound() {
        
        Authentication mockAuth = mock(Authentication.class);
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUsersUuid(mockAuth);
        });
    }

    @Test
    public void loadUserByUsername_userWithNoCharacters_returnsEmptyList() {
        UUID userWithNoCharsUuid = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO users (user_uuid, username, password, enabled)
            VALUES (?, ?, ?, ?)
            """,
            userWithNoCharsUuid, "emptyuser", "{noop}password", true
        );

        jdbcTemplate.update("""
            INSERT INTO authorities (username, authority)
            VALUES (?, ?)
            """,
            "emptyuser", "ROLE_USER"
        );

        CustomUserPrincipal result = (CustomUserPrincipal) userService.loadUserByUsername("emptyuser");
        
        assertNotNull(result);
        assertEquals("emptyuser", result.getUsername());
        assertTrue(result.getCharacters().isEmpty());
    }

    

}

