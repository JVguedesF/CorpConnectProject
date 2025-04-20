package com.CorpConnec.repository;

import com.CorpConnec.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends MongoRepository<Message, UUID> {
    Page<Message> findByRoomId(UUID roomId, Pageable pageable);

    @Query("{ 'content': { $regex: ?0, $options: 'i' }, 'room.$id': ?1 }")
    List<Message> findByContentContainingIgnoreCaseAndRoomId(String query, UUID roomId);
}