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

import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerView;
import com.scanakispersonalprojects.dndapp.model.webSocket.ContainerDeleteMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.ContainerRequestMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.WebSocketResponse;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.ContainerService;

/**
 * WebSocket controller for handling real-tiem container operations.
 * 
 * Handles subscription to containers and real time container 
 * modifications.
 * 
 * WebSocket endpoints:
 *  /app/container/subscribe - Subscribe to containers
 *  /app/container/delete - Deletes containers in real time
 * 
 * 
 */
@Controller
public class ContainerWebSocketController {
    private final static Logger LOG = Logger.getLogger(ContainerWebSocketController.class.getName());

    // Spring messaging tempalte for sending WebSocket messages to users and topics
    private final SimpMessagingTemplate messagingTemplate;

    // Services for handling container operations and data retrival
    private final ContainerService containerService;

    // Services for user authentication and character ownership validation
    private final CustomUserDetailsService userService;


    /**
     * Constructs a new ContainerWebSocketController with the required dependencies
     * 
     * @param messagingTemplate
     * @param containerService
     * @param userService
     */
    public ContainerWebSocketController(SimpMessagingTemplate messagingTemplate, ContainerService containerService, CustomUserDetailsService userService) {
        this.messagingTemplate = messagingTemplate;
        this.containerService = containerService;
        this.userService = userService;
    }

    /**
     * Handles WebSocket subscriptions request for containers
     * 
     * @param message - client WebSocket message - includes charUuid
     * @param principal - user authentication
     * @return  CONTAINER_REQUEST_RESPONSE - Succesful character data retrival
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     */

    @MessageMapping("container/subscribe")
    public void subscribeToContainers(@Payload ContainerRequestMessage message, Principal principal) {
        LOG.info("WebSocket container subscribe for character: " + message.getCharUuid());
        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            UUID characterUuid = message.getCharUuid();
            
            if(!characters.contains(characterUuid)) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }

            List<ContainerView> containerView = containerService.getCharactersContainers(characterUuid);

            if(!containerView.isEmpty()) {
                WebSocketResponse response = new WebSocketResponse(
                    "CONTAINER_REQUEST_RESPONSE",
                    true,
                    "Containers loaded succesfully",
                    containerView
                );

                sendToUser(principal.getName(), response);
                LOG.info("Sent conatiners list with " + containerView.size() + " containers to user: " + principal.getName());

            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Container not found");
            }
        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to update containers");
        }
    }

    /**
     * Deletes container in real time.
     * 
     * @param message - client WebScoekt Mesasge includes charUuid
     * @param principal - user authentication
     * 
     * @return  CONTAINER_DELETION_RESPONSE - Succesful character data retrival and upates
     *          UNAUTHORIZEZD - user doesn't own the requested character
     *          NOT_FOUND - Character data not found
     *          INTERNAL_ERROR - Server error during procesing
     *
     */
    @MessageMapping("container/delete")
    public void deleteContainer(@Payload ContainerDeleteMessage message, Principal principal) {
        LOG.info("WebSocket container delete for character" + message.getCharUuid());
        try {
            Authentication authentication = (Authentication) principal;
            List<UUID> characters = userService.getUsersCharacters(authentication);
            UUID characterUuid = message.getCharUuid();
            
            if(!characters.contains(characterUuid)) {
                sendErrorToUser(principal.getName(), "UNAUTHORIZED", "You don't own this character");
                return;
            }

            if(containerService.deleteContainer(message.getCharUuid(), message.getContainerUuid())) {
                WebSocketResponse response = new WebSocketResponse(
                    "CONTAINER_DELETION_RESPONSE",
                    true,
                    "Container succesfully deleted",
                    null
                );
                sendToUser(principal.getName(), response);
                broadcastContainers(message.getCharUuid(), response);

                LOG.info("WebScoekt - container succesfully deleted charUuid :" +  message.getCharUuid().toString() + ", containerUuid : " + message.getContainerUuid().toString());
            } else {
                sendErrorToUser(principal.getName(), "NOT_FOUND", "Container was not found or not empty");
            }
        } catch (Exception e) {
            sendErrorToUser(principal.getName(), "INTERNAL_ERROR", "Failed to delete container");
        }

    }

    /**
     * Sends a WebSocket response to a specific user's private queue.
     * 
     * @param username - the username of the target user
     * @param response - the {@link WebSocketResponse} to send to the user
     */
    private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/containers", response);
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
    public void broadcastContainers(UUID charUuid, WebSocketResponse response) {
        try {
            List<ContainerView> currentContainers = containerService.getCharactersContainers(charUuid);
            WebSocketResponse broadcastResponse = new WebSocketResponse(
                response.getType() + "BROADCAST",
                true,
                response.getMessage(),
                currentContainers
            );

            messagingTemplate.convertAndSend("/topic/character/" + charUuid + "/containers", broadcastResponse);
        } catch (Exception e) {
            LOG.warning("Failed to broadcast containers update for character: " + charUuid);
            messagingTemplate.convertAndSend("/topics/character/" + charUuid + "/container", response);
        }
        
    }

}
