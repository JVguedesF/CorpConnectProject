package com.CorpConnec.service;

import com.CorpConnec.exception.DuplicateRoomException;
import com.CorpConnec.exception.EntityNotFoundException;
import com.CorpConnec.exception.InvalidRoomException;
import com.CorpConnec.exception.UnauthorizedException;
import com.CorpConnec.model.dto.request.RoomRequestDto;
import com.CorpConnec.model.dto.response.RoomResponseDto;
import com.CorpConnec.model.entity.Room;
import com.CorpConnec.model.entity.RoomType;
import com.CorpConnec.model.entity.User;
import com.CorpConnec.model.mappers.RoomMapper;
import com.CorpConnec.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final UserService userService;
    private final RoomMapper roomMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RoomResponseDto createRoom(RoomRequestDto requestDto, UUID userId) {
        logger.info("Creating new room: {}", requestDto.name());

        User creator = userService.findActiveUserById(userId);
        validateRoomCreation(requestDto);

        Room room = roomMapper.toEntity(requestDto);
        configureNewRoom(room, creator, requestDto);

        Room savedRoom = roomRepository.save(room);
        logger.debug("Room created with ID: {}", savedRoom.getId());
        return roomMapper.toResponse(savedRoom);
    }

    private void validateRoomCreation(RoomRequestDto requestDto) { // Remova o parâmetro creator
        if (roomRepository.existsByName(requestDto.name())) {
            logger.warn("Duplicate room name: {}", requestDto.name());
            throw new DuplicateRoomException("Room name '" + requestDto.name() + "' already exists");
        }

        if (requestDto.type() == RoomType.PRIVATE && isInvalidPassword(requestDto.password())) {
            logger.warn("Attempt to create private room without password");
            throw new InvalidRoomException("Password is required for private rooms");
        }
    }

    private boolean isInvalidPassword(String password) {
        return password == null || password.isBlank();
    }

    private void configureNewRoom(Room room, User creator, RoomRequestDto requestDto) {
        room.setCreator(creator);
        room.setActive(true);

        if (requestDto.type() == RoomType.PRIVATE) {
            room.setPassword(passwordEncoder.encode(requestDto.password()));
        }
    }

    public RoomResponseDto getRoomById(UUID roomId) {
        logger.debug("Fetching room with ID: {}", roomId);
        Room room = roomRepository.findByIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> {
                    logger.error("Room not found: {}", roomId);
                    return new EntityNotFoundException("Room", roomId.toString());
                });
        return roomMapper.toResponse(room);
    }

    public Page<RoomResponseDto> getActivePublicRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return roomRepository.findByTypeAndIsActiveTrue(RoomType.PUBLIC, pageable)
                .map(roomMapper::toResponse);
    }

    public List<RoomResponseDto> getUserRooms(UUID userId) {
        List<Room> createdRooms = roomRepository.findByCreatorIdAndIsActiveTrue(userId);
        List<Room> participatingRooms = roomRepository.findByParticipantsIdAndIsActiveTrue(userId);

        return Stream.concat(createdRooms.stream(), participatingRooms.stream())
                .distinct()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Transactional
    public RoomResponseDto updateRoom(UUID roomId, RoomRequestDto requestDto, UUID userId) {
        logger.info("Updating room ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room", roomId.toString()));

        validateRoomOwnership(room, userId);
        validateRoomUpdate(requestDto, room);
        updateRoomFields(room, requestDto);

        Room updatedRoom = roomRepository.save(room);
        logger.debug("Room {} updated successfully", roomId);
        return roomMapper.toResponse(updatedRoom);
    }

    private void validateRoomOwnership(Room room, UUID userId) {
        if (!room.getCreator().getId().equals(userId)) {
            logger.warn("Unauthorized update attempt by user: {}", userId);
            throw new UnauthorizedException("Only room creator can modify the room");
        }
    }

    private void validateRoomUpdate(RoomRequestDto requestDto, Room room) {
        if (requestDto.name() != null
                && !requestDto.name().equals(room.getName())
                && roomRepository.existsByName(requestDto.name())) {
            logger.warn("Duplicate room name during update: {}", requestDto.name());
            throw new DuplicateRoomException("Room name already exists");
        }

        if (requestDto.type() == RoomType.PRIVATE
                && (requestDto.password() == null || requestDto.password().isBlank())) {
            logger.warn("Invalid password update for private room");
            throw new InvalidRoomException("Password required for private rooms");
        }
    }

    private void updateRoomFields(Room room, RoomRequestDto requestDto) {
        if (requestDto.name() != null && !requestDto.name().isBlank()) {
            room.setName(requestDto.name());
        }

        if (requestDto.description() != null) {
            room.setDescription(requestDto.description());
        }

        if (requestDto.type() != null) {
            room.setType(requestDto.type());
        }

        if (requestDto.password() != null && !requestDto.password().isBlank()) {
            room.setPassword(passwordEncoder.encode(requestDto.password()));
        }
    }

    @Transactional
    public void deleteRoom(UUID roomId, UUID userId) {
        logger.info("Deleting room ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room", roomId.toString()));

        validateRoomOwnership(room, userId);
        performSoftDelete(room);
        logger.debug("Room {} marked as inactive", roomId);
    }

    private void performSoftDelete(Room room) {
        room.setActive(false);
        roomRepository.save(room);
    }
}