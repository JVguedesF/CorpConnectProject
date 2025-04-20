package com.CorpConnec.model.mappers;

import com.CorpConnec.model.dto.request.MessageRequestDto;
import com.CorpConnec.model.dto.response.*;
import com.CorpConnec.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final UserMapper userMapper;
    private final FileAttachmentMapper fileAttachmentMapper;
    private final ReactionMapper reactionMapper;

    public Message toEntity(
            MessageRequestDto dto,
            User sender,
            Room room,
            List<FileAttachment> attachments,
            List<Reaction> reactions
    ) {
        return Message.builder()
                .content(dto.content())
                .sender(sender)
                .room(room)
                .attachments(attachments)
                .reactions(reactions)
                .isEncrypted(dto.isEncrypted())
                .build();
    }

    public MessageResponseDto toResponseDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                userMapper.toResponse(message.getSender()),
                message.getRoom().getId(),
                message.getTimestamp(),
                message.getAttachments().stream()
                        .map(fileAttachmentMapper::toResponseDto)
                        .collect(Collectors.toList()),
                message.getReactions().stream()
                        .map(reactionMapper::toResponseDto)
                        .collect(Collectors.toList()),
                message.isEncrypted(),
                message.isRead()
        );
    }
}