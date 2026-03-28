package com.education.education.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @MessageMapping("/chat/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public ChatMessage sendMessage(
            @DestinationVariable String groupId,
            @Payload ChatMessage message) {
        message.setGroupId(groupId);
        message.setSentAt(LocalDateTime.now());
        return message;
    }
}