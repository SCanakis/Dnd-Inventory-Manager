package com.scanakispersonalprojects.dndapp.controller.inventory;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;


import com.scanakispersonalprojects.dndapp.model.basicCharInfo.User;
import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.UserDaoPSQL;
import com.scanakispersonalprojects.dndapp.persistance.inventory.InventoryJPARepo;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ItemCatalogJPARepo;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.testutils.TestDataBuilder;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class InventoryControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryJPARepo inventoryRepo;
    
    @Autowired
    private ItemCatalogJPARepo itemRepo;

    @Autowired
    private UserDaoPSQL userRepo;

    @SuppressWarnings("removal")
    @MockBean
    private CustomUserDetailsService userDetailsService;

    // @Autowired
    // private JdbcTemplate jdbcTemplate;

    private User user;
    private UUID characterUuid;
    private ItemCatalog sword;
    private CharacterHasItemSlot swordSlot;

    @BeforeEach
    void setUp() {
        user = TestDataBuilder.defaultUser().build();
        userRepo.save(user);
        characterUuid = UUID.randomUUID();
        userRepo.addCharacterUser(characterUuid, characterUuid);
        sword = TestDataBuilder.longSword().build();
        swordSlot = TestDataBuilder.itemSlot(sword.getItemUuid(), characterUuid).build();
        itemRepo.save(sword);
        inventoryRepo.save(swordSlot);
    }

    void setupTestData() {
       
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getAllInventory_returns200() throws Exception {
        
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

        mockMvc.perform(get("/inventory/"+characterUuid))
            .andExpect(status().isOk());
    }

}
