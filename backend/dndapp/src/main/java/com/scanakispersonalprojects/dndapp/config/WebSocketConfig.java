package com.scanakispersonalprojects.dndapp.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 * 
 * WebSocket configuration class
 * 
 * This config class enables WebScoket messaging with STOMP protocal support,
 * allows real-time communciation between clients and the server.
 * 
 * Key features:
 *  Enables WeCoket message borker functionality
 *  Configures STOMP endpoitns for client connections
 *  Sets up message routing and destination prefixes
 *  Provides SockJS support for older browsers 
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
    

    private static final Logger logger = Logger.getLogger(WebSocketConfig.class.getName());


    /**
     * Registers STOMP endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("ws/character")
            .setAllowedOriginPatterns("*")
            .withSockJS();
            logger.info("WebSocket endpoint regsitered: /ws/character");
    }


    /**
     * Congiures the message borker for routing messages between cleints and the server
     */

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }


}
