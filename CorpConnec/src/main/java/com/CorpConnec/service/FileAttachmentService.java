package com.CorpConnec.service;

import com.CorpConnec.exception.AuthorizationException;
import com.CorpConnec.exception.EntityNotFoundException;
import com.CorpConnec.exception.FileProcessingException;
import com.CorpConnec.model.dto.request.FileAttachmentRequestDto;
import com.CorpConnec.model.dto.response.FileAttachmentResponseDto;
import com.CorpConnec.model.entity.FileAttachment;
import com.CorpConnec.model.mappers.FileAttachmentMapper;
import com.CorpConnec.repository.FileAttachmentRepository;
import com.CorpConnec.service.filestorage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileAttachmentService {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileAttachmentMapper fileAttachmentMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    public FileAttachmentResponseDto uploadFile(MultipartFile file, FileAttachmentRequestDto dto, UUID userId) {
        try {
            String fileType = dto.contentType().split("/")[0];
            Path storageDirectory = Paths.get("attachments", fileType.toLowerCase());
            String fileUri = fileStorageService.storeFile(file, storageDirectory);

            FileAttachment attachment = fileAttachmentMapper.toEntity(dto);
            attachment.setUri(fileUri);
            attachment.setSize(file.getSize());
            attachment.setUploadedAt(LocalDateTime.now());
            attachment.setUploadedBy(userId);

            FileAttachment savedAttachment = fileAttachmentRepository.save(attachment);
            return fileAttachmentMapper.toResponseDto(savedAttachment);
        } catch (IOException e) {
            throw new FileProcessingException("Error processing file: " + e.getMessage(), e);
        }
    }

    public FileAttachmentResponseDto getFileMetadata(UUID fileId) {
        return fileAttachmentRepository.findById(fileId)
                .map(fileAttachmentMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("File", fileId.toString()));
    }

    @Transactional
    public void deleteFile(UUID fileId, UUID userId) {
        try {
            FileAttachment attachment = fileAttachmentRepository.findById(fileId)
                    .orElseThrow(() -> new EntityNotFoundException("File", fileId.toString()));

            if (!attachment.getUploadedBy().equals(userId)) {
                throw new AuthorizationException("User not authorized to delete this file");
            }

            fileStorageService.deleteFile(attachment.getUri());
            fileAttachmentRepository.delete(attachment);
        } catch (IOException e) {
            throw new FileProcessingException("Error deleting file: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(UUID fileId) {
        try {
            FileAttachment attachment = fileAttachmentRepository.findById(fileId)
                    .orElseThrow(() -> new EntityNotFoundException("File", fileId.toString()));

            return fileStorageService.retrieveFile(attachment.getUri());
        } catch (IOException e) {
            throw new FileProcessingException("Error retrieving file: " + e.getMessage(), e);
        }
    }

    public List<FileAttachmentResponseDto> getFilesByUploader(UUID userId) {
        return fileAttachmentRepository.findByUploadedBy(userId)
                .stream()
                .map(fileAttachmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}