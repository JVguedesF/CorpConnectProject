package com.CorpConnec.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MessageResponseDto(
        UUID id,
        String content,
        UserResponseDto sender,
        UUID roomId,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp,
        List<FileAttachmentResponseDto> attachments,
        List<ReactionResponseDto> reactions,
        boolean isEncrypted,
        boolean isRead
) {}
