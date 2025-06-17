// package com.scanakispersonalprojects.dndapp.controller.basicCharInfo;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import org.springframework.security.test.context.support.WithMockUser;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;
// import org.testcontainers.junit.jupiter.Testcontainers;

// import com.fasterxml.jackson.annotation.JsonAutoDetect;
// import com.fasterxml.jackson.annotation.PropertyAccessor;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
// import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharViewPatch;

// import org.springframework.test.web.servlet.MockMvc;


// @SpringBootTest
// @AutoConfigureMockMvc
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @ActiveProfiles("test")
// @Transactional
// @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
// @Testcontainers
// public class BasicCharInfoControllerTest {
    
    
//     @Autowired 
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private JdbcTemplate jdbcTemplate;

//     private UUID testCharUuid;
//     private UUID testRaceUuid;
//     private UUID testBackgroundUuid;
//     private UUID testClassUuid;
//     private UUID testSubclassUuid;


    
//     @BeforeEach
//     void setUp() {
//         testCharUuid = UUID.randomUUID();
//         testRaceUuid = UUID.randomUUID();
//         testBackgroundUuid = UUID.randomUUID();
//         testClassUuid = UUID.randomUUID();
//         testSubclassUuid = UUID.randomUUID();

//         setupTestData();
//     }

//     private void setupTestData() {
        
//         jdbcTemplate.update("""
//         INSERT INTO race (race_uuid, name, stat_increase_str, stat_increase_dex, stat_increase_con,
//                           stat_increase_int, stat_increase_wis, stat_increase_cha)
//         VALUES (?, ?, ?, ?, ?, ?, ?, ?)
//         """,
//         testRaceUuid, "Human", 1, 1, 1, 0, 0, 0
//     );

//         // Insert test background
//         jdbcTemplate.update("""
//             INSERT INTO background (background_uuid, name, description, starting_gold)
//             VALUES (?, ?, ?, ?)
//             """,
//             testBackgroundUuid, "Acolyte", "Served in a temple", 15
//         );

//         // Insert test class
//         jdbcTemplate.update("""
//             INSERT INTO class (class_uuid, name, hit_dice_value, description)
//             VALUES (?, ?, ?, ?)
//             """,
//             testClassUuid, "Fighter", 10, "A master of martial combat"
//         );

//         // Insert test subclass
//         jdbcTemplate.update("""
//             INSERT INTO subclass (subclass_uuid, name, class_source)
//             VALUES (?, ?, ?)
//             """,
//             testSubclassUuid, "Champion", testClassUuid
//         );

//         // Insert test character info
//         jdbcTemplate.update("""
//             INSERT INTO characters_info 
//             (char_info_uuid, name, inspiration, background_uuid, race_uuid,
//             strength, dexterity, constitution, intelligence, wisdom, charisma)
//             VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
//             """,
//             testCharUuid, "Test Character", true, testBackgroundUuid, testRaceUuid,
//             15, 14, 13, 12, 10, 8
//         );

//         // Insert character class
//         jdbcTemplate.update("""
//             INSERT INTO character_class (char_info_uuid, class_uuid, subclass_uuid, level, hit_dice_remaining)
//             VALUES (?, ?, ?, ?, ?)
//             """,
//             testCharUuid, testClassUuid, testSubclassUuid, 5, 4
//         );

//         // Insert HP handler
//         jdbcTemplate.update("""
//             INSERT INTO hp_handler (char_info_uuid, current_hp, max_hp, temp_hp)
//             VALUES (?, ?, ?, ?)
//             """,
//             testCharUuid, 45, 50, 5
//         );

//         // Insert death saving throws
//         jdbcTemplate.update("""
//             INSERT INTO death_saving_throws (char_info_uuid, success, failure)
//             VALUES (?, ?, ?)
//             """,
//             testCharUuid, 2, 1
//         );

//         // Insert test user
//         UUID testUserUuid = UUID.randomUUID();
//         jdbcTemplate.update("""
//             INSERT INTO users (user_uuid, username, password, enabled)
//             VALUES (?, ?, ?, ?)
//             """,
//             testUserUuid, "testuser", "{noop}password123", true
//         );

//         // Grant ROLE_USER authority
//         jdbcTemplate.update("""
//             INSERT INTO authorities (username, authority)
//             VALUES (?, ?)
//             """,
//             "testuser", "ROLE_USER"
//         );

//         // Link user to character
//         jdbcTemplate.update("""
//             INSERT INTO users_characters (user_uuid, character_uuid)
//             VALUES (?, ?)
//             """,
//             testUserUuid, testCharUuid
//         );
//     }



//    @Test
//     public void getBasicCharInfo_whenUnknownId_returns401() throws Exception {
//         mockMvc.perform(get("/character/{id}", UUID.randomUUID()))
//                .andExpect(status().isUnauthorized());
//     }

//     @Test
//     @WithMockUser(username = "testuser", roles = {"USER"})
//     public void getBasicCharInfo_whenExistingId_returns200() throws Exception {
//         mockMvc.perform(get("/character/{id}", testCharUuid))
//                .andExpect(status().isOk());
//     }


//     @Test
//     @WithMockUser(username = "testuser", roles = {"USER"})
//     public void updateBasicCharInfo_Id_returns200() throws Exception {
        
//         objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

//         Map<UUID, Integer> hitDice = new HashMap<>();
//         hitDice.put(testClassUuid, 2);

//         Map<AbilityScore, Integer> as = new HashMap<>();
//         as.put(AbilityScore.strength, 29);

//         CharViewPatch patch = new CharViewPatch("Updated Test Character", 0, 50, hitDice, false, as, 3, 0);

//         String json = objectMapper.writeValueAsString(patch);
        
//         mockMvc.perform(
//             put("/character/{uuid}", testCharUuid)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(json)
//         )
//         .andExpect(status().isOk());
//     }


//     @Test
//     @WithMockUser(username = "testuser", roles = {"USER"})
//     public void updateBasicCharInfo_Id_returns401() throws Exception {
        
//         objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

//         Map<UUID, Integer> hitDice = new HashMap<>();
//         hitDice.put(testClassUuid, 2);

//         Map<AbilityScore, Integer> as = new HashMap<>();
//         as.put(AbilityScore.strength, 29);

//         CharViewPatch patch = new CharViewPatch("Updated Test Character", 0, 50, hitDice, false, as, 3, 0);

//         String json = objectMapper.writeValueAsString(patch);
        
//         mockMvc.perform(
//             put("/character/{uuid}", UUID.randomUUID())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(json)
//         )
//         .andExpect(status().isUnauthorized());
//     }

//     @Test
//     @WithMockUser(username = "testuser", roles = {"USER"})
//     public void deleteCharacter_returns401() throws Exception {

//         mockMvc.perform(
//             delete("/character/{uuid}", UUID.randomUUID()))
//             .andExpect(status().isUnauthorized());
//     }

//     @Test
//     @WithMockUser(username = "testuser", roles = {"USER"})
//     public void deleteCharacter_returns200() throws Exception {

//         mockMvc.perform(
//             delete("/character/{uuid}", testCharUuid))
//             .andExpect(status().isOk());
//     }



// }
