package com.scanakispersonalprojects.dndapp.controller.inventory;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import com.scanakispersonalprojects.dndapp.service.inventory.ItemCatalogService;


@WebMvcTest(value = CatalogController.class, 
           excludeAutoConfiguration = {SecurityAutoConfiguration.class})        
public class CatalogControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;


    @SuppressWarnings("removal")
    @MockBean
    private ItemCatalogService itemCatalogService;


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getItemUsingUUID_returns500() throws Exception {
        UUID testUuid = UUID.randomUUID();
        
        when(itemCatalogService.getItemWithUUID(testUuid))
            .thenThrow(new RuntimeException("Database connection failed"));
        
        mockMvc.perform(get("/itemCatalog/id={uuid}", testUuid))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getAll_returns500() throws Exception {
    
        when(itemCatalogService.getAll())
            .thenThrow(new RuntimeException("Database connection failed"));
        
        mockMvc.perform(get("/itemCatalog"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getBySearchTerm_returns500() throws Exception {
    
        String searchTerm = "sowrd";

        when(itemCatalogService.searchByName(searchTerm))
            .thenThrow(new RuntimeException("Database connection failed"));
        
        mockMvc.perform(get("/itemCatalog/searchTerm={searchTerm}", searchTerm))
            .andExpect(status().isInternalServerError());
    }

    
}
