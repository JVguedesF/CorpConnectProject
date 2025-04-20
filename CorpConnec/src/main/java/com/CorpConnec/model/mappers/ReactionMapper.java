package com.CorpConnec.model.mappers;

import com.CorpConnec.model.dto.request.ReactionRequestDto;
import com.CorpConnec.model.dto.response.ReactionResponseDto;
import com.CorpConnec.model.entity.Reaction;
import com.CorpConnec.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionMapper {

    private final UserMapper userMapper;

    public Reaction toEntity(ReactionRequestDto dto, User user) {
        return Reaction.builder()
                .emoji(dto.emoji())
                .user(user)
                .build();
    }

    public ReactionResponseDto toResponseDto(Reaction reaction) {
        return new ReactionResponseDto(
                reaction.getId(),
                reaction.getEmoji(),
                userMapper.toResponse(reaction.getUser()),
                reaction.getTimestamp()
        );
    }
}