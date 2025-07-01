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

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.webSocket.InventoryAddMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.InventoryDeleteMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.InventoryUpdateMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.WebSocketResponse;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.InventoryService;

@Controller
public class InventoryWebSocketController {
    
    private final static Logger LOG = Logger.getLogger(InventoryWebSocketController.class.getName());


    private final SimpMessagingTemplate messagingTemplate;

    private final InventoryService inventoryService;

    private final CustomUserDetailsService userService;

    public InventoryWebSocketController(SimpMessagingTemplate messagingTemplate, InventoryService inventoryService, CustomUserDetailsService userService) {
        this.messagingTemplate = messagingTemplate;
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    @MessageMapping("inventory/subscribe")
    public void subscribeToInventory(@Payload String charUuid, Principal principal) {
    LOG.info("WebSocket inventory subscribe for character: " + charUuid);
    try {
        Authentication authentication = (Authentication) principal;
        List<UUID> characters = userService.getUsersCharacters(authentication);
        UUID characterUuid = UUID.fromString(charUuid);
        
        if(!characters.contains(characterUuid)) {
            sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
            return;
        }
        
        List<CharacterHasItemProjection> currentInventory = inventoryService.getInventoryWithUUID(characterUuid);

        WebSocketResponse response = new WebSocketResponse(
            "INVENTORY_INITIAL_LOAD",
            true,
            "Initial inventory loaded successfully",
            currentInventory
        );
        
        sendToUser(principal.getName(), response);

        LOG.info("Sent initial inventory with " + currentInventory.size() + " items to user: " + principal.getName());

    } catch (Exception e) {
        LOG.warning("Failed to load initial inventory: " + e.getMessage());
        sendErrorToUser(charUuid, "INTERNAL_ERROR", "Failed to load initial inventory");
    }
}
    @MessageMapping("inventory/update")
    public void updateInventoryItem(@Payload InventoryUpdateMessage message, Principal principal) {
        LOG.info("WebSocket inventory update for character: " + message.getCharUuid());
        
        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            if(!characters.contains(message.getCharUuid())) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }

            CharacterHasItemSlot result = inventoryService.updateCharacterHasSlot(
                    message.getCharUuid(),
                    message.getItemUuid(),
                    message.getContainerUuid(),
                    message.getUpdate()
            );
            if(result != null) {
                WebSocketResponse response = new WebSocketResponse(
                    "INVENTORY_UPDATES_SUCCESS",
                    true,
                    "Item updated succesfully",
                    result
                );
                sendToUser(principal.getName(), response);

                broadcastInventoryUpdate(message.getCharUuid(), response);
            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Item or container not found");
            }

        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to update inventory");
        }
    
    }

    @MessageMapping("inventory/add")
    public void addItemInventory(@Payload InventoryAddMessage message, Principal principal) {
        LOG.info("WebSocket add to inventory for character: " + message.getCharUuid());
    
        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            if(!characters.contains(message.getCharUuid())) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }

            boolean result = inventoryService.saveItemToInventory(message.getItemUuid(), message.getCharUuid(), message.getQuantity());

            if(result != false) {
                WebSocketResponse response = new WebSocketResponse(
                    "INVENTORY_ADDITION_SUCCESS",
                    true,
                    "Item added succesfully",
                    result
                );
                sendToUser(principal.getName(), response);

                broadcastInventoryUpdate(message.getCharUuid(), response);

            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Item or container not found");
            }

        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to update inventory");
        }
    
    }   

    @MessageMapping("inventory/delete")
    public void deleteItemInventory(@Payload InventoryDeleteMessage message, Principal principal) {
        LOG.info("WebSocket add to inventory for character: " + message.getCharUuid());
    
        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            if(!characters.contains(message.getCharUuid())) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }

            boolean result = inventoryService.deleteItemFromInventory(message.getCharUuid(), message.getItemUuid(), message.getContainerUuid());

            if(result != false) {
                WebSocketResponse response = new WebSocketResponse(
                    "INVENTORY_DELETION_SUCCESS",
                    true,
                    "Item added succesfully",
                    result
                );
                sendToUser(principal.getName(), response);

                broadcastInventoryUpdate(message.getCharUuid(), response);

            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Item or container not found");
            }

        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to update inventory");
        }
    
    }   
     

    private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/inventory", response);
    }

    private void sendErrorToUser(String username,String errorType, String message) {
        WebSocketResponse error = new WebSocketResponse(errorType, false, message, null);
        sendToUser(username, error);
    }

    private void broadcastInventoryUpdate(UUID charUuid, WebSocketResponse response) {
        try {
            List<CharacterHasItemProjection> currentInventory = inventoryService.getInventoryWithUUID(charUuid);
            WebSocketResponse broadcastResponse = new WebSocketResponse(
                response.getType() + "BROADCAST",
                true,
                response.getMessage(),
                currentInventory
            );

            messagingTemplate.convertAndSend("/topic/character/" + charUuid + "/inventory", broadcastResponse);
        } catch (Exception e) {
            LOG.warning("Failed to broadcast inventory update for character: " + charUuid);
            messagingTemplate.convertAndSend("/topics/character/" + charUuid + "/inventory", response);
        }
        
    }


}
