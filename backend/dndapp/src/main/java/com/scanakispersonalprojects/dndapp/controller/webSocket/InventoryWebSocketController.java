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
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemUpdate;
import com.scanakispersonalprojects.dndapp.model.webSocket.InventoryAddMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.InventoryDeleteMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.InventoryRequestMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.InventoryUpdateMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.WebSocketResponse;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.InventoryService;


/**
 * WebSocket controller for handling real-time inventory operations.
 * 
 * Handles subscritpions to inventory and real time inventory operatinos.
 * 
 * WebSocket endpoints:
 *  /app/inventory/subscribe - Subscribe to containers
 *  /app/inventory/delete - Deletes containers in real time
 *  /app/inventory/update - Updates containers in real time
 *  /app/inventory/add - Adds containers in real time
 * 
 */
@Controller
public class InventoryWebSocketController {

    
    private final static Logger LOG = Logger.getLogger(InventoryWebSocketController.class.getName());

    // Spring messaging template for sending WebSocket messages to users and topics
    private final SimpMessagingTemplate messagingTemplate;

    // Services for handling container operations and data retrival
    private final InventoryService inventoryService;

    // Services for user authentication and character ownership validation
    private final CustomUserDetailsService userService;

    // Container Controller to update containers when inventory updates
    private final ContainerWebSocketController containerController;

    /**
     * Constructs a new InventoryWebSocketController with the required dependencies
     * 
     * @param messagingTemplate
     * @param inventoryService
     * @param userService
     * @param containerController
     */
    public InventoryWebSocketController(SimpMessagingTemplate messagingTemplate, InventoryService inventoryService, CustomUserDetailsService userService, ContainerWebSocketController containerController) {
        this.messagingTemplate = messagingTemplate;
        this.inventoryService = inventoryService;
        this.userService = userService;
        this.containerController = containerController;
    }

    /**
     * Handles WebSocket subscriptions request for inventory
     * 
     * @param message - client WebSocket message - includes charUuid
     * @param principal - user authentication
     * @return  INVENTORY_INITIAL_LOAD - Inventory was succesfully loaded
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     */

    @MessageMapping("inventory/subscribe")
    public void subscribeToInventory(@Payload InventoryRequestMessage message, Principal principal) {
    LOG.info("WebSocket inventory subscribe for character: " + message.getCharUuid());
        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            UUID characterUuid = message.getCharUuid();
            
            if(!characters.contains(characterUuid)) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }
            
            UUID containerUuid = message.getContainerUuid();
            String searchTerm = message.getSearchTerm();

            List<CharacterHasItemProjection> currentInventory = null;
            if(containerUuid != null && searchTerm != null) {
                currentInventory = inventoryService.getContainerItemsUsingFZF(characterUuid, containerUuid, searchTerm);
            } else if(message.getContainerUuid() != null && message.getSearchTerm() == null) {
                currentInventory = inventoryService.getItemsInContainer(characterUuid, containerUuid);
            } else if(message.getContainerUuid() == null && message.getSearchTerm() != null) {
                currentInventory = inventoryService.getInventoryUsingFZF(characterUuid, searchTerm);
            } else {
                currentInventory = inventoryService.getInventoryWithUUID(characterUuid);
            }

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
            sendErrorToUser(message.getCharUuid().toString(), "INTERNAL_ERROR", "Failed to load initial inventory");
        }
    }

    /**
     * Handles WebSocket inventory update requests
     * 
     * @param message - client WebSocket message - incldues {@link CharacterHasItemUpdate}
     * @param principal - user authentication
     * @return  INVENTORY_UPDATES_SUCCESS - inventory was succesfully updated
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     * 
     * Broadcasts containers when inventory is updated
     */

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
                this.containerController.broadcastContainers(message.getCharUuid(), response);
            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Item or container not found");
            }

        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to update inventory");
        }
    
    }


    /**
     * Handles WebSocket adding new items to inventory
     * 
     * @param message - client WebSocket message - includes itemUuid and quantity 
     * @param principal - user authentication
     * @return  INVENTORY_ADDITION_SUCCESS - item was succesfully added to inventory
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     */

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

    /**
     * Handles WebSocket deleting item from inventory
     * 
     * @param message - client WebSocket message - includes itemUuid 
     * @param principal - user authentication
     * @return  INVENTORY_DELETION_SUCCESS - item was succesfully deleted from inventory
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     */

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
                containerController.broadcastContainers(message.getCharUuid(), response);

            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Item or container not found");
            }

        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to update inventory");
        }
    
    }   
     
    /**
     * Sends a WebSocket response to a specific user's private queue.
     * 
     * @param username - the username of the target user
     * @param response - the {@link WebSocketResponse} to send to the user
     */
    private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/inventory", response);
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
