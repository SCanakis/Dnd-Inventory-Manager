package com.scanakispersonalprojects.dndapp.controller.basicCharInfo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfoUpdateDTO;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.HPHandler;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CharacterInfoService;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;

@WebMvcTest(value = BasicCharInfoController.class, 
           excludeAutoConfiguration = {SecurityAutoConfiguration.class})
           
public class BasicCharInfoControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @SuppressWarnings("removal")
    @MockBean
    private CharacterInfoService characterService;
    
    @SuppressWarnings("removal")
    @MockBean
    private CustomUserDetailsService userService;


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getCharacterBasicView_whenServiceThrowsException_returns500() throws Exception {
        UUID testUuid = UUID.randomUUID();

        when(characterService.getCharacterBasicInfoView(testUuid))
            .thenThrow(new RuntimeException("Database connection failed"));
        
        mockMvc.perform(get("/character/{uuid}", testUuid))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})  
    public void getCharacterBasicView_whenCharacterNotFound_returns404() throws Exception {
        UUID testUuid = UUID.randomUUID();

        when(characterService.getCharacterBasicInfoView(testUuid)).thenReturn(null);
        
        mockMvc.perform(get("/character/{uuid}", testUuid))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})  
    public void updateBasicCharInfo_Id_returns404() throws Exception {
        UUID testUuid = UUID.randomUUID();

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
        .thenReturn(List.of(testUuid));

        when(characterService.updateCharInfo(eq(testUuid), any(CharacterInfoUpdateDTO.class)))
            .thenReturn(null);
        
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        CharacterInfoUpdateDTO characterInfo = new CharacterInfoUpdateDTO();

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
        String json = objectMapper.writeValueAsString(characterInfo);

        mockMvc.perform(
            put("/character/{uuid}", testUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
        .andExpect(status().isNotFound());
    }
    

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})  
    public void deleteBasicCharInfo_return404() throws Exception {
        UUID testUuid = UUID.randomUUID();

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(testUuid));

        when(characterService.updateCharInfo(eq(testUuid), any(CharacterInfoUpdateDTO.class)))
            .thenReturn(null);

        mockMvc.perform(
            delete("/character/{uuid}", testUuid)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})  
    public void deleteBasicCharInfo_return500() throws Exception {
        UUID userTestUuid = UUID.randomUUID();
        UUID characterTestUuid = UUID.randomUUID();

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(characterTestUuid));

        when(userService.getUsersUuid(ArgumentMatchers.<Authentication>any()))
            .thenReturn(userTestUuid);

        when(characterService.deleteCharacter(characterTestUuid, userTestUuid))
            .thenThrow(new RuntimeException("Throws Exception"));

        mockMvc.perform(delete("/character/{uuid}", characterTestUuid))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})  
    public void updateBasicCharInfo_Id_returns500() throws Exception {
        
        UUID testUuid = UUID.randomUUID();
        // objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        CharacterInfoUpdateDTO characterInfo = new CharacterInfoUpdateDTO();

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

        String json = objectMapper.writeValueAsString(characterInfo);

        when(userService.getUsersCharacters(ArgumentMatchers.<Authentication>any()))
            .thenReturn(List.of(testUuid));

        when(characterService.updateUsingPatch(eq(testUuid), any(CharacterInfoUpdateDTO.class)))
            .thenThrow(new RuntimeException("Throws Exception"));

        mockMvc.perform(
            put("/character/{uuid}", testUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isInternalServerError());
    }

}