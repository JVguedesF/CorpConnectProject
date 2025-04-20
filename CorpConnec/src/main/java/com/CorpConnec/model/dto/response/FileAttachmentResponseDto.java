package com.CorpConnec.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileAttachmentResponseDto(
        UUID id,
        String filename,
        String contentType,
        String uri,
        long size,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime uploadedAt,
        boolean isEncrypted
) {}
