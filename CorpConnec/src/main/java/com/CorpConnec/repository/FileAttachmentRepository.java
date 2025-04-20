package com.CorpConnec.repository;

import com.CorpConnec.model.entity.FileAttachment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface FileAttachmentRepository extends MongoRepository<FileAttachment, UUID> {
    List<FileAttachment> findByUploadedBy(UUID uploadedBy);
}