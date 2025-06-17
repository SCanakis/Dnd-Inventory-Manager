package com.scanakispersonalprojects.dndapp.controller.basicCharInfo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClassDetail;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfoUpdateDTO;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.HPHandler;

import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class BasicCharInfoControllerTest {
    
    
    @Autowired 
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        
           // Insert test race with JSON stat_increases
        jdbcTemplate.update("""
            INSERT INTO race (race_uuid, name, stat_increases)
            VALUES (?, ?, ?::json)
            """,
            testRaceUuid, "aTestRace", "{\"strength\": 1, \"dexterity\": 1, \"constitution\": 1, \"intelligence\": 1, \"wisdom\": 1, \"charisma\": 1}"
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

        // Insert test character info with JSON columns
        jdbcTemplate.update("""
            INSERT INTO characters_info 
            (char_info_uuid, name, inspiration, background_uuid, race_uuid,
            ability_scores, hp_handler, death_saving_throws)
            VALUES (?, ?, ?, ?, ?, ?::json, ?::json, ?::json)
            """,
            testCharUuid, "aTestCharacter", true, testBackgroundUuid, testRaceUuid,
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
    public void getBasicCharInfo_whenUnknownId_returns401() throws Exception {
        mockMvc.perform(get("/character/{id}", UUID.randomUUID()))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "aTestUser", roles = {"USER"})
    public void getBasicCharInfo_whenExistingId_returns200() throws Exception {
        mockMvc.perform(get("/character/{id}", testCharUuid))
               .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "aTestUser", roles = {"USER"})
    public void updateBasicCharInfo_Id_returns200() throws Exception {
        
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

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
                 

        String json = objectMapper.writeValueAsString(classDetail);
        
        mockMvc.perform(
            put("/character/{uuid}", testCharUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
        .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "aTestUser", roles = {"USER"})
    public void updateBasicCharInfo_Id_returns401() throws Exception {
        
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

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
     

        String json = objectMapper.writeValueAsString(characterInfo);
        
        mockMvc.perform(
            put("/character/{uuid}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "aTestUser", roles = {"USER"})
    public void deleteCharacter_returns401() throws Exception {

        mockMvc.perform(
            delete("/character/{uuid}", UUID.randomUUID()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "aTestUser", roles = {"USER"})
    public void deleteCharacter_returns200() throws Exception {

        mockMvc.perform(
            delete("/character/{uuid}", testCharUuid))
            .andExpect(status().isOk());
    }



}
