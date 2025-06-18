package com.scanakispersonalprojects.dndapp.controller.inventory;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.InventoryService;
import com.scanakispersonalprojects.dndapp.service.inventory.ItemCatalogService;

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

    @SuppressWarnings("removal")
    @MockBean
    private InventoryService inventoryService;

    @SuppressWarnings("removal")
    @MockBean
    private ItemCatalogService itemCatalogService;

    private UUID characterUuid;
    private UUID itemUuid = UUID.randomUUID();

    private static final UUID TEST_CHARACTER_UUID = UUID.fromString("eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9");

    private final UUID emptyContainerUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");



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
    
    @Test
    @WithMockUser(username = "NOT_AUTHORIZED", roles = {"USER"})
    public void getInventoryUsingUUID_returns401() throws Exception{
        
        mockMvc.perform(get("/inventory/" + characterUuid))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void getInventoryUsingUUID_returns404() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));
        
        when(inventoryService.getInventoryWithUUID(characterUuid))
            .thenReturn(null);
        
        mockMvc.perform(get("/inventory/" + characterUuid))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void getInventoryUsingUUID_returns500() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));
        
        when(inventoryService.getInventoryWithUUID(characterUuid))
            .thenThrow(new RuntimeException("Test Exception"));
        
        mockMvc.perform(get("/inventory/" + characterUuid))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getInventoryUsingFZF_returns200() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

        mockMvc.perform(get("/inventory/" + characterUuid + "/searchTerm=sword"))
            .andExpect((status().isOk()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getInventoryUsingFZF_returns404() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

        when(inventoryService.getInventoryUsingFZF(characterUuid, "sword"))
            .thenReturn(null);

        mockMvc.perform(get("/inventory/" + characterUuid + "/searchTerm=sword"))
            .andExpect((status().isNotFound()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getInventoryUsingFZF_returns500() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

        when(inventoryService.getInventoryUsingFZF(characterUuid, "sword"))
            .thenThrow(new RuntimeException("Throw Exception"));

        mockMvc.perform(get("/inventory/" + characterUuid + "/searchTerm=sword"))
            .andExpect((status().isInternalServerError()));
    }

    @Test
    @WithMockUser(username = "NOT_AUTHORIZED", roles = {"USER"})
    public void getInventoryUsingFZF_returns401() throws Exception {   
        mockMvc.perform(get("/inventory/" + characterUuid + "/searchTerm=sword"))
            .andExpect((status().isUnauthorized()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getItemFromInventory_returns200() throws Exception {   
        when(itemCatalogService.getItemWithUUID(itemUuid))
            .thenReturn(new ItemCatalog());
        
        mockMvc.perform(get("/inventory/" + characterUuid + "/id=" + itemUuid))
            .andExpect((status().isOk()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getItemFromInventory_returns404() throws Exception {   
        when(itemCatalogService.getItemWithUUID(itemUuid))
            .thenReturn(null);
        
        mockMvc.perform(get("/inventory/" + characterUuid + "/id=" + itemUuid))
            .andExpect((status().isNotFound()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getItemFromInventory_returns500() throws Exception {   
        when(itemCatalogService.getItemWithUUID(itemUuid))
            .thenThrow(new RuntimeException("Throw Exception"));
        
        mockMvc.perform(get("/inventory/" + characterUuid + "/id=" + itemUuid))
            .andExpect((status().isInternalServerError()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void deleteItemFromInventory_returns200() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

        when(inventoryService.deleteItemFromInventory(characterUuid, itemUuid, emptyContainerUuid))
            .thenReturn(true);

        mockMvc.perform(delete("/inventory/" + characterUuid + "/id=" + itemUuid + "/containerId=" + emptyContainerUuid))
            .andExpect((status().isOk()));
    }

    @Test
    @WithMockUser(username = "NOT_AUTHORIZED", roles = {"USER"})
    public void deleteItemFromInventory_returns401() throws Exception {

        mockMvc.perform(delete("/inventory/" + characterUuid + "/id=" + itemUuid + "/containerId=" + emptyContainerUuid))
            .andExpect((status().isUnauthorized()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void deleteItemFromInventory_returns404() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

        when(inventoryService.deleteItemFromInventory(characterUuid, itemUuid, emptyContainerUuid))
            .thenReturn(null);

        mockMvc.perform(delete("/inventory/" + characterUuid + "/id=" + itemUuid + "/containerId=" + emptyContainerUuid))
            .andExpect((status().isNotFound()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void deleteItemFromInventory_returns_second_404() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

        when(inventoryService.deleteItemFromInventory(characterUuid, itemUuid, emptyContainerUuid))
            .thenReturn(false);

        mockMvc.perform(delete("/inventory/" + characterUuid + "/id=" + itemUuid + "/containerId=" + emptyContainerUuid))
            .andExpect((status().isNotFound()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void deleteItemFromInventory_returns500() throws Exception {
        when(userDetailsService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterUuid));

        when(inventoryService.deleteItemFromInventory(characterUuid, itemUuid, emptyContainerUuid))
            .thenThrow(new RuntimeException("Exception thrown"));

        mockMvc.perform(delete("/inventory/" + characterUuid + "/id=" + itemUuid + "/containerId=" + emptyContainerUuid))
            .andExpect((status().isInternalServerError()));
    }

    



}