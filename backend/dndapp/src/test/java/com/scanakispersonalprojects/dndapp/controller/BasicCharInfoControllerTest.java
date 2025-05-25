package com.scanakispersonalprojects.dndapp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;
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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharViewPatch;

import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
    public void getBasicCharInfo_whenUnknownId_returns404() throws Exception {
        mockMvc.perform(get("/character/{id}", UUID.randomUUID()))
               .andExpect(status().isNotFound());
    }

    @Test
    public void getBasicCharInfo_whenExistingId_returns200() throws Exception {
        mockMvc.perform(get("/character/{id}", testCharUuid))
               .andExpect(status().isOk());
    }


    @Test
    public void updateBasicCharInfo_Id_returns200() throws Exception {
        
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Map<UUID, Integer> hitDice = new HashMap<>();
        hitDice.put(testClassUuid, 2);

        Map<AbilityScore, Integer> as = new HashMap<>();
        as.put(AbilityScore.STRENGTH, 29);

        CharViewPatch patch = new CharViewPatch("Updated Test Character", 0, 50, hitDice, false, as, 3, 0);

        String json = objectMapper.writeValueAsString(patch);
        
        mockMvc.perform(
            put("/character/{id}", testCharUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
        .andExpect(status().isOk());
    }


    @Test
    public void updateBasicCharInfo_Id_returns404() throws Exception {
        
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Map<UUID, Integer> hitDice = new HashMap<>();
        hitDice.put(testClassUuid, 2);

        Map<AbilityScore, Integer> as = new HashMap<>();
        as.put(AbilityScore.STRENGTH, 29);

        CharViewPatch patch = new CharViewPatch("Updated Test Character", 0, 50, hitDice, false, as, 3, 0);

        String json = objectMapper.writeValueAsString(patch);
        
        mockMvc.perform(
            put("/character/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
        .andExpect(status().isNotFound());
    }

}
