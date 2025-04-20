package com.CorpConnec.controller;


import com.CorpConnec.model.dto.request.RoomRequestDto;
import com.CorpConnec.model.dto.response.RoomResponseDto;
import com.CorpConnec.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto requestDto) {
        UUID userId = getAuthenticatedUserId();
        RoomResponseDto response = roomService.createRoom(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable UUID roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @GetMapping("/public")
    public ResponseEntity<Page<RoomResponseDto>> getPublicRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(roomService.getActivePublicRooms(page, size));
    }

    @GetMapping("/my-rooms")
    public ResponseEntity<List<RoomResponseDto>> getUserRooms() {
        UUID userId = getAuthenticatedUserId();
        return ResponseEntity.ok(roomService.getUserRooms(userId));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable UUID roomId,
            @RequestBody RoomRequestDto requestDto) {
        UUID userId = getAuthenticatedUserId();
        return ResponseEntity.ok(roomService.updateRoom(roomId, requestDto, userId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId) {
        UUID userId = getAuthenticatedUserId();
        roomService.deleteRoom(roomId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UUID) authentication.getPrincipal();
    }
}