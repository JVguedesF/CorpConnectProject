package com.CorpConnec.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record FileAttachmentRequestDto(
        @NotBlank(message = "Filename is required")
        String filename,

        @NotBlank(message = "Content type is required")
        String contentType,

        @NotBlank(message = "URI is required")
        String uri,

        @Positive(message = "Size must be positive")
        long size,

        boolean isEncrypted
) {}
