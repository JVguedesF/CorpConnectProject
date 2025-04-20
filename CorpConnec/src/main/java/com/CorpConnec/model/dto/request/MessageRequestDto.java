package com.CorpConnec.model.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record MessageRequestDto(
        @NotBlank(message = "Content is required")
        String content,

        @NotBlank(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "Room ID is required")
        UUID roomId,

        List<String> attachments,

        List<ReactionRequestDto> reactions,

        boolean isEncrypted
) {
}