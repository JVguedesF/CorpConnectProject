package com.CorpConnec.model.mappers;

import com.CorpConnec.model.dto.request.FileAttachmentRequestDto;
import com.CorpConnec.model.dto.response.FileAttachmentResponseDto;
import com.CorpConnec.model.entity.FileAttachment;
import org.springframework.stereotype.Component;

@Component
public class FileAttachmentMapper {

    public FileAttachment toEntity(FileAttachmentRequestDto dto) {
        return FileAttachment.builder()
                .filename(dto.filename())
                .contentType(dto.contentType())
                .uri(dto.uri())
                .size(dto.size())
                .isEncrypted(dto.isEncrypted())
                .build();
    }

    public FileAttachmentResponseDto toResponseDto(FileAttachment attachment) {
        return new FileAttachmentResponseDto(
                attachment.getId(),
                attachment.getFilename(),
                attachment.getContentType(),
                attachment.getUri(),
                attachment.getSize(),
                attachment.getUploadedAt(),
                attachment.isEncrypted()
        );
    }
}