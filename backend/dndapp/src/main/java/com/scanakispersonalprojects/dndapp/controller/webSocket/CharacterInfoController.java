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


/**
 * WebSocket controller for handling real-tiem character information operations.
 * 
 * Handles subscription to character data updates and real time character stat 
 * modifications.
 * 
 * WebSocket endpoints:
 *  /app/character-stats/subscribe - Subscribe to character stats
 *  /app/character-stats/updates - Update character stats in real-time
 *  
 * 
 */

@Controller
public class CharacterInfoController {
    
    private final static Logger LOG = Logger.getLogger(CharacterInfoController.class.getName());

    // Srping messaging tempalte for sending WebScoekt messages to users and topics
    private final SimpMessagingTemplate messagingTemplate;

    // Services for handling character inforamtion operations and data retrival
    private final CharacterInfoService characterInfoService;

    // Servies for user authenticaiton and character owernserhip validation
    private final CustomUserDetailsService userService;


    /**
     * Constructs a new CharacterInfoController with the required dependencies
     * 
     * @param messagingTemplate
     * @param characterInfoService
     * @param userService
     */
    public CharacterInfoController(SimpMessagingTemplate messagingTemplate, CharacterInfoService characterInfoService, CustomUserDetailsService userService) {
        this.messagingTemplate = messagingTemplate;
        this.characterInfoService = characterInfoService;
        this.userService = userService;
    }


    /**
     * Handles WebSocket subscriptions request for character stats
     * 
     * @param message - client WebSocket message - includes charUuid
     * @param principal - user authentication 
     * 
     * @return  CHARACTER_STAT_LOAD - Succesful character data retrival
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     */

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


    /**
     * 
     * Updates character stats in real time.
     * 
     * @param message - client WebSocket Message incldues {@link CharacterInfoUpdateDTO}
     * @param principal - user authentication
     * @return  CHARACTER_STAT_LOAD - Succesful character data retrival and upates
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     * 
     */
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


    /**
     * Sends a WebSocket response to a specific user's private queue.
     * 
     * @param username - the username of the target user
     * @param response - the {@link WebSocketResponse} to send to the user
     */
    private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/character-stats", response);
    }

    /**
     * Sends an error response to a spceific user via their private queue.
     * 
     * @param username - the username of the target user
     * @param errorType - the type/category of error (e.g., "UNAOTHOIRZED, NOT_FOUND")
     * @param message - the descriptive error message for the user
     */
    private void sendErrorToUser(String username,String errorType, String message) {
        WebSocketResponse error = new WebSocketResponse(errorType, false, message, null);
        sendToUser(username, error);
    }


    /**
     * Broadcasts character updates to all subscribers of a specific character's topic.
     * 
     * @param charUuid - character uuid
     * @param response - the original {@link WebSocketResponse} to base the broadcast
     */
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
