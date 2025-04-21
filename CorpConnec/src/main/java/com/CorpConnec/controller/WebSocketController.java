package com.CorpConnec.controller;

import com.CorpConnec.model.dto.response.MessageResponseDto;
import com.CorpConnec.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public MessageResponseDto sendMessage(@Payload com.CorpConnec.model.dto.request.MessageRequestDto message,
                                          @DestinationVariable UUID roomId,
                                          SimpMessageHeaderAccessor headerAccessor) {

        Principal user = headerAccessor.getUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        if (user.getPrincipal() instanceof Jwt jwt) {
            UUID userId = UUID.fromString(jwt.getSubject());
            return messageService.createMessage(message, userId);
        } else {
            throw new RuntimeException("Unsupported authentication type");
        }
    }
}