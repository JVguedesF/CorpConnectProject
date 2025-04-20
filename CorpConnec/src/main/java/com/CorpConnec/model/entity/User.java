package com.CorpConnec.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String name;

    private String username;

    private String password;

    private String email;

    private Status status;

    private Role role;

    private boolean active = true;
}
