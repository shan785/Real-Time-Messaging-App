package com.myProjects.messagingApp.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@EnableWebSocket
@Configuration
public class WebsocketConfig implements WebSocketConfigurer {

    private WebsocketHandler websocketHandler;

    public WebsocketConfig(WebsocketHandler websocketHandler) {
        this.websocketHandler = websocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(websocketHandler, "/websocket")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor());

    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new CustomHandshakeInterceptor();
    }


}
