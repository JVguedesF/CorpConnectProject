package com.CorpConnec.service.filestorage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.storage.root-directory:uploads}")
    private String rootDirectory;

    @Override
    public Path getRootLocation() {
        return Paths.get(rootDirectory).toAbsolutePath().normalize();
    }

    @Override
    public String storeFile(MultipartFile file, Path directory) throws IOException {
        Path targetLocation = prepareStoragePath(directory);
        String filename = generateUniqueFilename(file.getOriginalFilename());

        Files.createDirectories(targetLocation);
        Files.copy(file.getInputStream(), targetLocation.resolve(filename));

        return targetLocation.resolve(filename).toString();
    }

    @Override
    public byte[] retrieveFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    private Path prepareStoragePath(Path directory) {
        Path fullPath = getRootLocation().resolve(directory);
        return fullPath.normalize();
    }

    private String generateUniqueFilename(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }
}