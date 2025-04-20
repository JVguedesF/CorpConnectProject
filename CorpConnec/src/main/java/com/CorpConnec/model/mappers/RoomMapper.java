package com.CorpConnec.model.mappers;

import com.CorpConnec.model.dto.request.RoomRequestDto;
import com.CorpConnec.model.dto.response.RoomResponseDto;
import com.CorpConnec.model.entity.Room;
import com.CorpConnec.model.entity.RoomType;
import com.CorpConnec.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomMapper {

    private final PasswordEncoder passwordEncoder;

    public Room toEntity(RoomRequestDto dto) {
        if (dto == null) {
            return null;
        }


        User creator = User.builder()
                .id(dto.creatorUserId())
                .build();

        return Room.builder()
                .name(dto.name())
                .description(dto.description())
                .type(dto.type())
                .creator(creator)
                .isActive(dto.isActive())
                .password(dto.type() == RoomType.PRIVATE ? passwordEncoder.encode(dto.password()) : null)
                .build();
    }

    public RoomResponseDto toResponse(Room room) {
        if (room == null) {
            return null;
        }
        return new RoomResponseDto(
                room.getId(),
                room.getName(),
                room.getDescription(),
                room.getType(),
                room.getCreator().getId(),
                room.isActive(),
                room.getCreatedAt(),
                room.getParticipants().stream()
                        .map(User::getId)
                        .toList()
        );
    }
}
