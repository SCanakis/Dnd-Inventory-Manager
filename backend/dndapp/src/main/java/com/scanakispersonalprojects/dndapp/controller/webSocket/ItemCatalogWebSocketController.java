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

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemProjection;
import com.scanakispersonalprojects.dndapp.model.webSocket.ItemCatalogRequestMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.WebSocketResponse;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.ItemCatalogService;

/**
 * WebSocket controller for handling real-time inventory operations.
 * 
 * Handles subscritpions to inventory and real time inventory operatinos.
 * 
 * WebSocket endpoints:
 *  /app/itemCatalog/subscribe - Subscribe to itemCatalog
 * 
 */
@Controller
public class ItemCatalogWebSocketController {
    private final static Logger LOG = Logger.getLogger(ItemCatalogWebSocketController.class.getName());

    // Spring messaging template for sending WebSocket messages to users and topics
    private final SimpMessagingTemplate messagingTemplate;

    // Services for handling itemCatalog operations and data retrival
    private final ItemCatalogService itemCatalogService;

    // Services for user authentication and character ownership validation
    private final CustomUserDetailsService userService;


    /**
     * Constructs a new ItemCatalogWebSocketController with the required dependencies
     * 
     * @param messagingTemplate
     * @param itemCatalogService
     * @param userService
     */
    public ItemCatalogWebSocketController(SimpMessagingTemplate messagingTemplate, ItemCatalogService itemCatalogService, CustomUserDetailsService userService) {
        this.messagingTemplate = messagingTemplate;
        this.itemCatalogService = itemCatalogService;
        this.userService = userService;
    }

    /**
     * Handles WebSocket subscriptions requests for itemCatalog
     * 
     * @param message - client WebSocket message - includes searchTerm
     * @param principal - user authentication
     * @return  ITEM_CATALOG_REQUEST_RESPONSE - ItemCatalog was succesfully loaded
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     * 
     */
    @MessageMapping("itemCatalog/subscribe")
    public void subscribeToItemCatalog(@Payload ItemCatalogRequestMessage message, Principal principal) {
        LOG.info("WebSocket get ItemCatalog for character: " + message.getCharUuid());
        try {
           Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            UUID charUuid = message.getCharUuid();

            if(!characters.contains(charUuid)) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }

            List<ItemProjection> items = null;
            if(message.getSearchTerm() != null) {
                items = itemCatalogService.searchByName(message.getSearchTerm());
            } else {
                items = itemCatalogService.getAll();
            } 

            WebSocketResponse response = new WebSocketResponse(
                "ITEM_CATALOG_REQUEST_RESPONSE",
                true,
                "ItemCatalog loaded succesfully",
                items
            );

            sendToUser(principal.getName(), response);

            LOG.info("Sent item Catalog to user: " + principal.getName());


        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to get ItemCatalog");
        }

    }

    /**
     * Sends a WebSocket response to a specific user's private queue.
     * 
     * @param username - the username of the target user
     * @param response - the {@link WebSocketResponse} to send to the user
     */
    private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/itemCatalog", response);
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

}
