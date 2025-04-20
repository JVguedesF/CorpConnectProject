package com.CorpConnec.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "file_attachments")
public class FileAttachment {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String filename;
    private String contentType;
    private String uri;
    private long size;

    @Field("uploaded_by")
    private UUID uploadedBy;

    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Builder.Default
    private boolean isEncrypted = false;
}