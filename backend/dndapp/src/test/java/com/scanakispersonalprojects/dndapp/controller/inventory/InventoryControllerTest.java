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

import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;

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


    @SuppressWarnings("removal")
    @MockBean
    private CustomUserDetailsService userDetailsService;

    private UUID characterUuid;
  

    // Use the same UUID that exists in your test schema
    private static final UUID TEST_CHARACTER_UUID = UUID.fromString("eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9");



    @BeforeEach
    void setUp() {
        characterUuid = TEST_CHARACTER_UUID;
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getAllInventory_returns200() throws Exception {
        
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

            mockMvc.perform(get("/inventory/" + characterUuid))
                .andExpect((status().isOk()));
    }
}