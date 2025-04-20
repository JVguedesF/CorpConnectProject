package com.CorpConnec.model.dto.response;

import com.CorpConnec.model.entity.RoomType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoomResponseDto(
        UUID id,
        String name,
        String description,
        RoomType type,
        UUID creatorId,
        boolean isActive,
        LocalDateTime createdAt,
        List<UUID> participantIds
) {}