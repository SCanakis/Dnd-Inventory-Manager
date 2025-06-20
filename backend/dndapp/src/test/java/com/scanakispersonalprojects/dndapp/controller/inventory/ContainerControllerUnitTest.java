package com.scanakispersonalprojects.dndapp.controller.inventory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.Container;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerView;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.ContainerService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class ContainerControllerUnitTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private ContainerService containerService;

    @SuppressWarnings("removal")
    @MockBean
    private CustomUserDetailsService userService;

    private final UUID thorinUuid = UUID.fromString("eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9");


    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void getContainer_returns200() throws Exception {
    
        // when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
        //     .thenReturn(List.of(thorinUuid));

        when(containerService.getCharactersContainers(thorinUuid))
            .thenReturn(new ArrayList<ContainerView>() {{
                add(new ContainerView());
            }});

        mockMvc.perform(get("/containers/" + thorinUuid))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void getContainer_returns404() throws Exception {
        UUID random = UUID.randomUUID();

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(random));
        
        mockMvc.perform(get("/containers/" + random))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void getContainer_returns500() throws Exception {        
        when(containerService.getCharactersContainers(thorinUuid))
            .thenThrow(new RuntimeException());

        mockMvc.perform(get("/containers/" + thorinUuid))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void createContainer_returns200() throws Exception {
        
        Container container = new Container();
        container.setCharUuid(thorinUuid);
        container.setMaxCapacity(500);
        container.setItemUuid(UUID.randomUUID());

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.createContainer(eq(thorinUuid),any(Container.class)))
            .thenReturn(container);

        mockMvc.perform(post("/containers/" + thorinUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(container)))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void createContainer_returns500() throws Exception {
        
        Container container = new Container();
        container.setCharUuid(thorinUuid);
        container.setMaxCapacity(500);
        container.setItemUuid(UUID.randomUUID());

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.createContainer(eq(thorinUuid),any(Container.class)))
            .thenThrow(new RuntimeException());

        mockMvc.perform(post("/containers/" + thorinUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(container)))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void createContainer_returns404() throws Exception {
        
        Container container = new Container();
        container.setCharUuid(thorinUuid);
        container.setMaxCapacity(500);

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.createContainer(eq(thorinUuid),any(Container.class)))
            .thenReturn(null);

        mockMvc.perform(post("/containers/" + thorinUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(container)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "IS_NOT_AUTHORIZED", roles={"USER"})
    public void createContainer_returns401() throws Exception {
        
        Container container = new Container();
        container.setCharUuid(thorinUuid);
        container.setMaxCapacity(500);

        when(containerService.createContainer(eq(thorinUuid),any(Container.class)))
            .thenReturn(null);

        mockMvc.perform(post("/containers/" + thorinUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(container)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void deleteContainer_returns200() throws Exception {
        
        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.deleteContainer(eq(thorinUuid),any(UUID.class)))
            .thenReturn(true);

        mockMvc.perform(delete("/containers/" + thorinUuid + "/containerId=" + UUID.randomUUID()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void deleteContainer_returns500() throws Exception {
        
        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.deleteContainer(eq(thorinUuid),any(UUID.class)))
            .thenThrow(new RuntimeException());

        mockMvc.perform(delete("/containers/" + thorinUuid + "/containerId=" + UUID.randomUUID()))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void deleteContainer_returns404() throws Exception {
        
        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.deleteContainer(eq(thorinUuid),any(UUID.class)))
            .thenReturn(false);

        mockMvc.perform(delete("/containers/" + thorinUuid + "/containerId=" + UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "IS_NOT_AUTHORIZED", roles={"USER"})
    public void deleteContainer_returns401() throws Exception {
        
        when(containerService.deleteContainer(eq(thorinUuid),any(UUID.class)))
            .thenReturn(false);

        mockMvc.perform(delete("/containers/" + thorinUuid + "/containerId=" + UUID.randomUUID()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void updateMaxCapacityOfContainer_returns200() throws Exception {
        
        Container container = new Container();

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.updateMaxCapacityOfContainer(eq(thorinUuid),any(UUID.class), anyInt()))
            .thenReturn(container);

        mockMvc.perform(put("/containers/" + thorinUuid + "/containerId=" + UUID.randomUUID())
            .param("maxCapacity", "40"))
            .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void updateMaxCapacityOfContainer_returns500() throws Exception {
        
        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.updateMaxCapacityOfContainer(eq(thorinUuid),any(UUID.class), anyInt()))
            .thenThrow(new RuntimeException());

        mockMvc.perform(put("/containers/" + thorinUuid + "/containerId=" + UUID.randomUUID())
            .param("maxCapacity", "40"))
            .andExpect(status().isInternalServerError());

    }
    
    @Test
    @WithMockUser(username = "testuser", roles={"USER"})
    public void updateMaxCapacityOfContainer_returns404() throws Exception {
        
        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(thorinUuid));
        
        when(containerService.updateMaxCapacityOfContainer(eq(thorinUuid),any(UUID.class), anyInt()))
            .thenReturn(null);

        mockMvc.perform(put("/containers/" + thorinUuid + "/containerId=" + UUID.randomUUID())
            .param("maxCapacity", "40"))
            .andExpect(status().isNotFound());

    }
    
    @Test
    @WithMockUser(username = "IS_NOT_AUTHORIZED", roles={"USER"})
    public void updateMaxCapacityOfContainer_returns401() throws Exception {
        
        when(containerService.updateMaxCapacityOfContainer(eq(thorinUuid),any(UUID.class), anyInt()))
            .thenReturn(null);

        mockMvc.perform(put("/containers/" + thorinUuid + "/containerId=" + UUID.randomUUID())
            .param("maxCapacity", "40"))
            .andExpect(status().isUnauthorized());

    }
    
   


}
