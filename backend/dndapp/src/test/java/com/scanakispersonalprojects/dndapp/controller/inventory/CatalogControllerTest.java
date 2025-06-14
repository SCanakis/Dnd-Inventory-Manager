package com.scanakispersonalprojects.dndapp.controller.inventory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.RollType;
import com.scanakispersonalprojects.dndapp.model.inventory.EquippableType;
import com.scanakispersonalprojects.dndapp.model.inventory.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.Rarity;
import com.scanakispersonalprojects.dndapp.model.inventory.Skill;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ItemCatalogJPARepo;

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
    private ItemCatalogJPARepo itemCatalogRepo;

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
        ItemCatalog testItem = new ItemCatalog(
            null,
            "LongSword",
            "it's a long sword",
            5,
            10,
            Rarity.common,
            true,
            (short) 2,
            new HashMap<AbilityScore, Boolean>() {{
                put(AbilityScore.strength, true);
            }},
            true,
            false,
            new ArrayList<EquippableType>() {{
                add(EquippableType.mainhand);
                add(EquippableType.offhand);
                add(EquippableType.twohand);
            }},
            new HashMap<AbilityScore, Integer>(){{
                put(AbilityScore.strength, 15);
                put(AbilityScore.dexterity, 12);
            }},
            new HashMap<Skill, RollType>() {{
                put(Skill.acrobatics, RollType.advantage);
                put(Skill.stealth, RollType.straight);
            }},
            new HashMap<Skill, Integer>(){{
                put(Skill.acrobatics, 3);
                put(Skill.sleight_of_hand, 5);
            }}
        );
        testItem = itemCatalogRepo.save(testItem);
        testItemUuid = testItem.getItemUuid();
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
