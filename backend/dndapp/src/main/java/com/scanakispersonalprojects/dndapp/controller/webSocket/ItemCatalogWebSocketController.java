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

@Controller
public class ItemCatalogWebSocketController {
    private final static Logger LOG = Logger.getLogger(ItemCatalogWebSocketController.class.getName());

    private final SimpMessagingTemplate messagingTemplate;

    private final ItemCatalogService itemCatalogService;

    private final CustomUserDetailsService userService;

    public ItemCatalogWebSocketController(SimpMessagingTemplate messagingTemplate, ItemCatalogService itemCatalogService, CustomUserDetailsService userService) {
        this.messagingTemplate = messagingTemplate;
        this.itemCatalogService = itemCatalogService;
        this.userService = userService;
    }

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

    private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/itemCatalog", response);
    }

    private void sendErrorToUser(String username,String errorType, String message) {
        WebSocketResponse error = new WebSocketResponse(errorType, false, message, null);
        sendToUser(username, error);
    }

}
