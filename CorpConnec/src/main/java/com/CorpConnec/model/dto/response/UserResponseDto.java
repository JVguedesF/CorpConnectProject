package com.CorpConnec.model.dto.response;

import com.CorpConnec.model.entity.Role;
import com.CorpConnec.model.entity.Status;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String username,
        String email,
        Status status,
        Role role
) {
}
