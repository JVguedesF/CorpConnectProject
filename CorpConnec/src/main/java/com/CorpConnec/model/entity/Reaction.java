package com.CorpConnec.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String emoji;

    private User user;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}