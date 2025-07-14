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

import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurse;
import com.scanakispersonalprojects.dndapp.model.webSocket.CoinPurseRequestMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.CoinPurseUpdateMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.WebSocketResponse;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.coinPurse.CoinPurseService;

@Controller
public class CoinPurseController {
    
    private static final Logger LOG = Logger.getLogger(CoinPurseController.class.getName());

    private final SimpMessagingTemplate messagingTemplate;

    private final CoinPurseService coinPurseService;

    private final CustomUserDetailsService userService;

    public CoinPurseController(SimpMessagingTemplate messagingTemplate, CoinPurseService coinPurseService,
            CustomUserDetailsService userService) {
        this.messagingTemplate = messagingTemplate;
        this.coinPurseService = coinPurseService;
        this.userService = userService;
    }

    @MessageMapping("coin-purse/subscribe")
    public void subscribeToCoinPurse(@Payload CoinPurseRequestMessage message, Principal principal) {
        LOG.info("WebSocket coin-purse subscriber to character: " + message.getCharUuid());

        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            UUID charUuid = message.getCharUuid();

            if(!characters.contains(charUuid)) {
                sendErrorToUser(principal.getName(), "UNAUTHROIZED", "You don't own this character");
                return;
            }

            CoinPurse coinPurse = coinPurseService.getCoinPurse(charUuid);
            if(coinPurse != null) {
                WebSocketResponse response = new WebSocketResponse(
                    "COIN-PURSE-LOAD",
                    true,
                    "Loading coin purse",
                    coinPurse
                );
                sendToUser(principal.getName(), response);
            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Coin Purse was not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to load coin purse");
        }
    }

    @MessageMapping("coin-purse/update")
    public void updateCoinPurse(@Payload CoinPurseUpdateMessage message, Principal principal) {
        LOG.info("WebSocket coin-purse update to character: " + message.getCharUuid());

        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            UUID charUuid = message.getCharUuid();

            if(!characters.contains(charUuid)) {
                sendErrorToUser(principal.getName(), "UNAUTHROIZED", "You don't own this character");
                return;
            } 

            if(coinPurseService.updateCoinPurse(charUuid, message.getCoinPurseDTO())) {
                WebSocketResponse response = new WebSocketResponse(
                    "COIN-PURSE-UPDATE",
                    true,
                    "Updated coin purse",
                    true
                );
                sendToUser(principal.getName(), response);
                broadcastCoinPurseUpdate(charUuid, response);
            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Coin Purse was not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to update coin purse");
        }
    }
    

    /**
     * Sends a WebSocket response to a specific user's private queue.
     * 
     * @param username - the username of the target user
     * @param response - the {@link WebSocketResponse} to send to the user
     */
    private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/coin-purse", response);
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
    private void broadcastCoinPurseUpdate(UUID charUuid, WebSocketResponse response) {
        try {
            CoinPurse coinPurse = coinPurseService.getCoinPurse(charUuid);
            WebSocketResponse broadcastResponse = new WebSocketResponse(
                response.getType() + "BROADCAST",
                true,
                response.getMessage(),
                coinPurse
            );

            messagingTemplate.convertAndSend("/topic/character/" + charUuid + "/coin-purse", broadcastResponse);
        } catch (Exception e) {
            LOG.warning("Failed to broadcast coin-purse update for character: " + charUuid);
            messagingTemplate.convertAndSend("/topics/character/" + charUuid + "/coin-purse", response);
        }
        
    }

}
