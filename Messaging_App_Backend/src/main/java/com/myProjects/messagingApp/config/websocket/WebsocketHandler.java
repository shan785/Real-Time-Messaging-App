package com.myProjects.messagingApp.config.websocket;

import com.myProjects.messagingApp.dto.MessageDto;
import com.myProjects.messagingApp.entity.MessageEntity;
import com.myProjects.messagingApp.enums.ActiveStatus;
import com.myProjects.messagingApp.enums.PayloadType;
import com.myProjects.messagingApp.repository.MessageRepo;
import com.myProjects.messagingApp.util.CustomPayload;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> activeUsers = new ConcurrentHashMap<>();

    private MessageRepo messageRepo;

    public WebsocketHandler(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {


        MessageDto messageDto = objectMapper.readValue(message.getPayload(), MessageDto.class);
        Long receiverId = messageDto.getReceiverId();
        String content = messageDto.getContent();

        if(activeUsers.containsKey(receiverId) && activeUsers.get(receiverId).isOpen()) {

            CustomPayload customPayload = new CustomPayload();
            customPayload.setPayloadType(PayloadType.MESSAGE);
            customPayload.setMessageDto(messageDto);
            customPayload.setActiveStatus(null);
            customPayload.setContactUserId(null);
            customPayload.setOnlineUsers(null);

            String json = objectMapper.writeValueAsString(customPayload);
            activeUsers.get(receiverId).sendMessage(new TextMessage(json));

            System.out.println(messageDto.getTimestamp());

            messageRepo.save(new MessageEntity(messageDto.getSenderId(), messageDto.getReceiverId(),
                                         messageDto.getContent(), messageDto.getTimestamp()));
        }
        else
            System.out.println("Storing the message in buffer [id: " + receiverId + ", message: " + content + "]");

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long connectedUserId = Long.parseLong((String) session.getAttributes().get("id"));

        if(!activeUsers.containsKey(connectedUserId)) {
           activeUsers.put(connectedUserId, session);
        }

        MessageDto messageDto = null;
        CustomPayload customPayload = new CustomPayload();

        customPayload.setPayloadType(PayloadType.ACTIVE_STATUS);
        customPayload.setActiveStatus(ActiveStatus.ONLINE);
        customPayload.setContactUserId(connectedUserId);
        customPayload.setMessageDto(null);
        customPayload.setOnlineUsers(null);

        String json = objectMapper.writeValueAsString(customPayload);
        List<Long> onlineUsers = new ArrayList<>();

        for(Map.Entry<Long, WebSocketSession> set : activeUsers.entrySet()) {
            if(set.getValue() == session)
                continue;

            set.getValue().sendMessage(new TextMessage(json));
            onlineUsers.add(set.getKey());
        }

        customPayload = new CustomPayload();
        customPayload.setPayloadType(PayloadType.ACTIVE_USERS);
        customPayload.setOnlineUsers(onlineUsers);
        customPayload.setContactUserId(null);
        customPayload.setMessageDto(null);
        customPayload.setActiveStatus(null);

        json = objectMapper.writeValueAsString(customPayload);
        session.sendMessage(new TextMessage(json));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) throws IOException {

        Long currentUserId = null;

        // Find disconnected user
        for (Map.Entry<Long, WebSocketSession> entry : activeUsers.entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                currentUserId = entry.getKey();
                break;
            }
        }

        // Remove disconnected user
        if (currentUserId != null) {
            activeUsers.remove(currentUserId);
        }

        // Create offline notification
        CustomPayload customPayload = new CustomPayload();
        customPayload.setPayloadType(PayloadType.ACTIVE_STATUS);
        customPayload.setActiveStatus(ActiveStatus.OFFLINE);
        customPayload.setContactUserId(currentUserId);

        String json = objectMapper.writeValueAsString(customPayload);

        // Broadcast to remaining connected users
        if(!activeUsers.isEmpty()) {
            for (WebSocketSession activeSession : activeUsers.values()) {
                if (activeSession.isOpen()) {
                    activeSession.sendMessage(new TextMessage(json));
                }
            }
        }
    }
}
