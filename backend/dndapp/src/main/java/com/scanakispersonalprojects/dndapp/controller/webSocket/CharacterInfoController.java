package com.scanakispersonalprojects.dndapp.controller.webSocket;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.webSocket.CharacterStatRequestMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.CharacterStatsUpdateMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.WebSocketResponse;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CharacterInfoService;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;

@Controller
public class CharacterInfoController {
    
    private final static Logger LOG = Logger.getLogger(CharacterInfoController.class.getName());

    private final SimpMessagingTemplate messagingTemplate;

    private final CharacterInfoService characterInfoService;

    private final CustomUserDetailsService userService;

    public CharacterInfoController(SimpMessagingTemplate messagingTemplate, CharacterInfoService characterInfoService, CustomUserDetailsService userService) {
        this.messagingTemplate = messagingTemplate;
        this.characterInfoService = characterInfoService;
        this.userService = userService;
    }

    @MessageMapping("character-stats/subscribe")
    public void subscribeToCharStats(@Payload CharacterStatRequestMessage message, Principal principal) {
        LOG.info("WebSocket character-stat subscribe for character: " + message.getCharUuid());

        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            UUID charUuid = message.getCharUuid();
            if(!characters.contains(charUuid)) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }

            CharacterBasicInfoView result = characterInfoService.getCharacterBasicInfoView(charUuid);
            if(result != null) {
                WebSocketResponse response = new WebSocketResponse(
                    "CHARACTER_STAT_LOAD",
                    true,
                    "Loading Character Stats",
                    result
                );
                sendToUser(principal.getName(), response);
                
            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Character Stats where not Found");
            }
        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to load character stats");
        }
    }

    @MessageMapping("character-stats/update")
    public void updateCharacterStats(@Payload CharacterStatsUpdateMessage message, Principal principal) {
        LOG.info("WebSocket character-stat update for character: " + message.getCharUuid());

        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            UUID charUuid = message.getCharUuid();
            if(!characters.contains(charUuid)) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }

            CharacterBasicInfoView result = characterInfoService.updateUsingPatch(charUuid, message.getUpdate());
            if(result != null) {
                WebSocketResponse response = new WebSocketResponse(
                    "CHARACTER_STAT_LOAD",
                    true,
                    "Loading Character Stats",
                    result
                );
                broadcastInventoryUpdate(charUuid, response);
                sendToUser(principal.getName(), response);
            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Character Stats where not Found");
            }
        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to update character stats");
        }
    }


    private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/character-stats", response);
    }

    private void sendErrorToUser(String username,String errorType, String message) {
        WebSocketResponse error = new WebSocketResponse(errorType, false, message, null);
        sendToUser(username, error);
    }

    private void broadcastInventoryUpdate(UUID charUuid, WebSocketResponse response) {
        try {
            CharacterBasicInfoView view = characterInfoService.getCharacterBasicInfoView(charUuid);
            WebSocketResponse broadcastResponse = new WebSocketResponse(
                response.getType() + "BROADCAST",
                true,
                response.getMessage(),
                view
            );

            messagingTemplate.convertAndSend("/topic/character/" + charUuid + "/character-stats", broadcastResponse);
        } catch (Exception e) {
            LOG.warning("Failed to broadcast character-stats update for character: " + charUuid);
            messagingTemplate.convertAndSend("/topics/character/" + charUuid + "/character-stats", response);
        }
        
    }

}
