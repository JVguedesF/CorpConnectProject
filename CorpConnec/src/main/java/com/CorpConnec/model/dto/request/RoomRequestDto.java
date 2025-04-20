package com.CorpConnec.model.dto.request;

import com.CorpConnec.model.entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RoomRequestDto(
        @NotBlank(message = "Room name is required")
        String name,

        String description,

        @NotNull(message = "Room type is required")
        RoomType type,

        @NotBlank(message = "Creator ID is required")
        UUID creatorUserId,

        boolean isActive,

        @Size(min = 4, message = "Password must have at least 4 characters")
        String password
) {}
