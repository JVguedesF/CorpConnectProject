package com.CorpConnec.controller;

import com.CorpConnec.model.dto.request.FileAttachmentRequestDto;
import com.CorpConnec.model.dto.response.FileAttachmentResponseDto;
import com.CorpConnec.service.FileAttachmentService;
import com.CorpConnec.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileAttachmentController {

    private final FileAttachmentService fileAttachmentService;
    private final AuthService authService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileAttachmentResponseDto> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") FileAttachmentRequestDto dto
    ) {
        UUID userId = authService.getAuthenticatedUserId();
        return ResponseEntity.ok(fileAttachmentService.uploadFile(file, dto, userId));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileAttachmentResponseDto> getFileMetadata(@PathVariable UUID fileId) {
        return ResponseEntity.ok(fileAttachmentService.getFileMetadata(fileId));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID fileId) {
        UUID userId = authService.getAuthenticatedUserId();
        fileAttachmentService.deleteFile(fileId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID fileId) {
        byte[] fileContent = fileAttachmentService.downloadFile(fileId);
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/uploader/{userId}")
    public ResponseEntity<List<FileAttachmentResponseDto>> getFilesByUploader(@PathVariable UUID userId) {
        authService.verifyOwnership(userId);
        return ResponseEntity.ok(fileAttachmentService.getFilesByUploader(userId));
    }
}