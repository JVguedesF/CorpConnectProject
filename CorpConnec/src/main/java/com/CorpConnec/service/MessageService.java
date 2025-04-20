package com.CorpConnec.service;

import com.CorpConnec.exception.EntityNotFoundException;
import com.CorpConnec.exception.InvalidMessageException;
import com.CorpConnec.model.dto.request.MessageRequestDto;
import com.CorpConnec.model.dto.response.MessageResponseDto;
import com.CorpConnec.model.entity.*;
import com.CorpConnec.model.mappers.MessageMapper;
import com.CorpConnec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final MessageMapper messageMapper;
    private final AuthService authService;

    @Transactional
    public MessageResponseDto createMessage(MessageRequestDto dto, UUID userId) {
        User sender = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId.toString()));

        Room room = roomRepository.findByIdAndIsActiveTrue(dto.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Room", dto.roomId().toString()));

        validateMessageContent(dto);

        List<UUID> attachmentIds = dto.attachments().stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        List<FileAttachment> attachments = fileAttachmentRepository.findAllById(attachmentIds);

        Message message = messageMapper.toEntity(
                dto,
                sender,
                room,
                attachments,
                Collections.emptyList()
        );
        message.setTimestamp(LocalDateTime.now());

        return messageMapper.toResponseDto(messageRepository.save(message));
    }

    private void validateMessageContent(MessageRequestDto dto) {
        if (dto.content() == null && (dto.attachments() == null || dto.attachments().isEmpty())) {
            throw new InvalidMessageException("Message must contain content or attachments");
        }
    }

    public Page<MessageResponseDto> getMessagesByRoom(UUID roomId, Pageable pageable) {
        return messageRepository.findByRoomId(roomId, pageable)
                .map(messageMapper::toResponseDto);
    }

    public MessageResponseDto getMessageById(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Message", messageId.toString()));
    }

    @Transactional
    public void deleteMessage(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message", messageId.toString()));

        authService.verifyOwnership(message.getSender().getId());
        messageRepository.delete(message);
    }

    @Transactional
    public MessageResponseDto updateMessage(UUID messageId, MessageRequestDto dto, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message", messageId.toString()));

        authService.verifyOwnership(message.getSender().getId());

        if (dto.content() != null) {
            message.setContent(dto.content());
        }

        if (dto.attachments() != null && !dto.attachments().isEmpty()) {
            List<UUID> newAttachmentIds = dto.attachments().stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
            List<FileAttachment> newAttachments = fileAttachmentRepository.findAllById(newAttachmentIds);
            message.setAttachments(newAttachments);
        }

        return messageMapper.toResponseDto(messageRepository.save(message));
    }

    public List<MessageResponseDto> searchMessages(String query, UUID roomId) {
        return messageRepository.findByContentContainingIgnoreCaseAndRoomId(query, roomId)
                .stream()
                .map(messageMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}