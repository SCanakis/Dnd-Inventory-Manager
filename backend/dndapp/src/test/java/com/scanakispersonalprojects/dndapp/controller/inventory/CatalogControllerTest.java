package com.scanakispersonalprojects.dndapp.controller.inventory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.RollType;
import com.scanakispersonalprojects.dndapp.model.inventory.EquippableType;
import com.scanakispersonalprojects.dndapp.model.inventory.Rarity;
import com.scanakispersonalprojects.dndapp.model.inventory.Skill;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class CatalogControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID testItemUuid;

    @BeforeEach
    void setUp() {
        testItemUuid = UUID.randomUUID();
        try {
            setupTestData();
        } catch (DataAccessException | JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private void setupTestData() throws DataAccessException, JsonProcessingException {
        jdbcTemplate.update("""
                INSERT INTO item_catalog (
                    item_uuid,
                    item_name, 
                    item_description, 
                    item_weight, 
                    item_value, 
                    item_rarity,
                    attackable,
                    equippable,
                    attunable,
                    item_equippable_type,
                    ability_requirment,
                    skill_altered_roll_type,
                    skill_altered_bonus
                ) VALUES (?,?,?,?,?,?::rarity,?,?,?,ARRAY[?]::equippable_type[],?::json,?::json,?::json)
                """, 
                testItemUuid,
                "Longsword",
                "A versatile martial weapon",
                3,
                15,
                Rarity.COMMON.getjsonValue(),
                true,
                true,
                true,
                new String[]{EquippableType.ARMOR.getjsonValue(), EquippableType.CLOAK.getjsonValue()},
                objectMapper.writeValueAsString(new HashMap<AbilityScore, Integer>() {{
                        put(AbilityScore.DEXTERITY, 15);
                    }}
                ),
                objectMapper.writeValueAsString(new HashMap<Skill, RollType>() {{
                        put(Skill.ACROBATICS, RollType.ADVANTAGE);
                    }}
                ),
                objectMapper.writeValueAsString(new HashMap<Skill, Integer>() {{
                        put(Skill.ACROBATICS, 5);
                    }}
                ));


                UUID testUserUuid = UUID.randomUUID();
                jdbcTemplate.update("""
                    INSERT INTO users (user_uuid, username, password, enabled)
                    VALUES (?, ?, ?, ?)
                    """,
                    testUserUuid, "testuser", "{noop}password123", true
                );

                jdbcTemplate.update("""
                    INSERT INTO authorities (username, authority)
                    VALUES (?, ?)
                    """,
                    "testuser", "ROLE_USER"
                );
    }

    // @Test
    // public void debugDataInsertion() throws Exception {
    //     // Check if the item was actually inserted
    //     Integer count = jdbcTemplate.queryForObject(
    //         "SELECT COUNT(*) FROM item_catalog WHERE item_uuid = ?", 
    //         Integer.class, 
    //         testItemUuid
    //     );
    //     System.out.println("Items found in database: " + count);
        
    //     // Check what's actually in the database
    //     List<Map<String, Object>> items = jdbcTemplate.queryForList(
    //         "SELECT * FROM item_catalog WHERE item_uuid = ?", 
    //         testItemUuid
    //     );
    //     System.out.println("Database content: " + items);
    // }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getItemUsingUUID_returns200() throws Exception {
        mockMvc.perform(get("/itemCatalog/id=" + testItemUuid))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getAllItemCatalog_returns200() throws Exception {
        mockMvc.perform(get("/itemCatalog"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getItemUsingFuzzySearch_returns200() throws Exception {
        mockMvc.perform(get("/itemCatalog/searchTerm=sword"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getItemUsingUUID_returns404() throws Exception {
        mockMvc.perform(get("/itemCatalog/id=" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }


}
