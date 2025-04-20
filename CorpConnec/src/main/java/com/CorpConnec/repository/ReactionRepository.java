package com.CorpConnec.repository;

import com.CorpConnec.model.entity.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ReactionRepository extends MongoRepository<Reaction, UUID> {
    List<Reaction> findByUserId(UUID userId);
    List<Reaction> findByEmoji(String emoji);
}