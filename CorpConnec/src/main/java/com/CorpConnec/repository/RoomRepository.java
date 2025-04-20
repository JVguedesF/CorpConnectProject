package com.CorpConnec.repository;

import com.CorpConnec.model.entity.Room;
import com.CorpConnec.model.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends MongoRepository<Room, UUID> {
    boolean existsByName(String name);

    Page<Room> findByTypeAndIsActiveTrue(RoomType type, Pageable pageable);

    List<Room> findByCreatorIdAndIsActiveTrue(UUID creatorId);

    List<Room> findByParticipantsIdAndIsActiveTrue(UUID participantId);

    @Query("{ 'participants.id': ?0, 'isActive': true }")
    List<Room> findRoomsByParticipantId(UUID participantId);

    Optional<Room> findByIdAndIsActiveTrue(UUID id);
}
