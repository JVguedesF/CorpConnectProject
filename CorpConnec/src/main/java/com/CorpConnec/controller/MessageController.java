package com.CorpConnec.controller;

import com.CorpConnec.model.dto.request.MessageRequestDto;
import com.CorpConnec.model.dto.response.MessageResponseDto;
import com.CorpConnec.service.AuthService;
import com.CorpConnec.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createMessage(@RequestBody MessageRequestDto dto) {
        UUID userId = authService.getAuthenticatedUserId();
        return ResponseEntity.ok(messageService.createMessage(dto, userId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Page<MessageResponseDto>> getMessagesByRoom(
            @PathVariable UUID roomId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(messageService.getMessagesByRoom(roomId, pageable));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> getMessageById(@PathVariable UUID messageId) {
        return ResponseEntity.ok(messageService.getMessageById(messageId));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        UUID userId = authService.getAuthenticatedUserId();
        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageRequestDto dto
    ) {
        UUID userId = authService.getAuthenticatedUserId();
        return ResponseEntity.ok(messageService.updateMessage(messageId, dto, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MessageResponseDto>> searchMessages(
            @RequestParam String query,
            @RequestParam UUID roomId
    ) {
        return ResponseEntity.ok(messageService.searchMessages(query, roomId));
    }
}