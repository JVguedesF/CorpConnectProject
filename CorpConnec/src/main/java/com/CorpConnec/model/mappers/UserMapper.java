package com.CorpConnec.model.mappers;

import com.CorpConnec.model.dto.request.UserRequestDto;
import com.CorpConnec.model.dto.response.UserResponseDto;
import com.CorpConnec.model.entity.Role;
import com.CorpConnec.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(UserRequestDto dto) {
        return User.builder()
                .name(dto.name())
                .username(dto.username())
                .password(passwordEncoder.encode(dto.password()))
                .email(dto.email())
                .status(dto.status())
                .role(Role.USER)
                .build();
    }

    public UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus(),
                user.getRole()
        );
    }
}