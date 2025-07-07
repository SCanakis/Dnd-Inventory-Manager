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
import com.scanakispersonalprojects.dndapp.model.webSocket.ContainerRequestMessage;
import com.scanakispersonalprojects.dndapp.model.webSocket.WebSocketResponse;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.ContainerService;

@Controller
public class ContainerWebSocketController {
    private final static Logger LOG = Logger.getLogger(ContainerWebSocketController.class.getName());

    private final SimpMessagingTemplate messagingTemplate;

    private final ContainerService containerService;

    private final CustomUserDetailsService userService;

    public ContainerWebSocketController(SimpMessagingTemplate messagingTemplate, ContainerService containerService, CustomUserDetailsService userService) {
        this.messagingTemplate = messagingTemplate;
        this.containerService = containerService;
        this.userService = userService;
    }

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

      private void sendToUser(String username, WebSocketResponse response) {
        messagingTemplate.convertAndSendToUser(username, "/queue/containers", response);
    }

    private void sendErrorToUser(String username,String errorType, String message) {
        WebSocketResponse error = new WebSocketResponse(errorType, false, message, null);
        sendToUser(username, error);
    }

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
