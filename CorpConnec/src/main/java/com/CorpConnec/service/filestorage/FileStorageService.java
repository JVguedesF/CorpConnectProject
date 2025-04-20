package com.CorpConnec.service.filestorage;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    String storeFile(MultipartFile file, Path directory) throws IOException;
    byte[] retrieveFile(String filePath) throws IOException;
    void deleteFile(String filePath) throws IOException;
    Path getRootLocation();
}