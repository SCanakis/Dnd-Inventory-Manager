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
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CustomUserPrincipal;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;
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
        assertEquals("Test Character", actualChar.name());
        assertTrue(actualChar.inspiration());
        assertEquals("Acolyte", actualChar.background());
        assertEquals(testBackgroundUuid, actualChar.backgroundUUID());
        assertEquals("Human", actualChar.race());
        assertEquals(testRaceUuid, actualChar.raceUUID());
        
        Map<AbilityScore, Integer> abilityScores = actualChar.abilityScores();
        assertEquals(Integer.valueOf(15), abilityScores.get(AbilityScore.strength));
        assertEquals(Integer.valueOf(14), abilityScores.get(AbilityScore.dexterity));
        assertEquals(Integer.valueOf(13), abilityScores.get(AbilityScore.constitution));
        assertEquals(Integer.valueOf(12), abilityScores.get(AbilityScore.intelligence));
        assertEquals(Integer.valueOf(10), abilityScores.get(AbilityScore.wisdom));
        assertEquals(Integer.valueOf(8), abilityScores.get(AbilityScore.charisma));
        
        List<DndClass> classes = actualChar.classes();
        assertNotNull(classes);
        assertEquals(1, classes.size());
        
        DndClass dndClass = classes.get(0);
        assertEquals("Fighter", dndClass.className()); 
        assertEquals(5, dndClass.level()); 

        HPHandler hpHandler = actualChar.hpHandler();
        assertNotNull(hpHandler);
        assertEquals(45, hpHandler.currentHp()); 

        assertEquals(50, hpHandler.maxHp()); 

        assertEquals(5, hpHandler.tempHp());

        DeathSavingThrowsHelper deathSaves = actualChar.deathSavingThrowsHelper();
        assertNotNull(deathSaves);
        assertEquals(2, deathSaves.success());
        assertEquals(1, deathSaves.failure()); 
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

