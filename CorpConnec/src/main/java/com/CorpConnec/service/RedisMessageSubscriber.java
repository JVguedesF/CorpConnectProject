package com.CorpConnec.service;

import com.CorpConnec.model.dto.response.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessageSubscriber.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            MessageResponseDto messageDto = (MessageResponseDto) redisTemplate.getValueSerializer()
                    .deserialize(message.getBody());

            if (messageDto != null) {
                messagingTemplate.convertAndSend("/topic/room/" + messageDto.roomId(), messageDto);
                logger.debug("Message forwarded to WebSocket: {}", messageDto.id());
            }
        } catch (Exception e) {
            logger.error("Error processing Redis message", e);
        }
    }
}