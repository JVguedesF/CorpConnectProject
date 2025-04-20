package com.CorpConnec.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rooms")
public class Room {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String name;

    private String description;

    @Builder.Default
    private RoomType type = RoomType.PUBLIC;

    @DBRef
    private User creator;

    @DBRef
    @Builder.Default
    private List<User> participants = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isActive = true;

    private String password;
}