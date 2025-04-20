package com.CorpConnec.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReactionResponseDto(
        UUID id,
        String emoji,
        UserResponseDto user,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp
) {}
