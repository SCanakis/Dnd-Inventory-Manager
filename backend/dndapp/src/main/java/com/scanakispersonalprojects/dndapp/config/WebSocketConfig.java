package com.scanakispersonalprojects.dndapp.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
    

    private static final Logger logger = Logger.getLogger(WebSocketConfig.class.getName());



    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("ws/character")
            .setAllowedOriginPatterns("*")
            .withSockJS();
            logger.info("WebSocket endpoint regsitered: /ws/character");
            // .setStreamBytesLimit(512 * 1024)
            // .setHttpMessageCacheSize(1000)
            // .setDisconnectDelay(30 * 1000);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
        // registry.setUserDestinationPrefix("/user");
    }






}
