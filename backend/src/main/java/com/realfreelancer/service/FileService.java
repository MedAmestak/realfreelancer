package com.realfreelancer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileService {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.file.base-url:http://localhost:8080/files}")
    private String baseUrl;

    public String uploadFile(MultipartFile file, String username) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Create user-specific directory
        Path userPath = uploadPath.resolve(username);
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String filename = timestamp + "_" + uniqueId + fileExtension;

        // Save file
        Path filePath = userPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return baseUrl + "/" + username + "/" + filename;
    }

    public boolean deleteFile(String filename, String username) {
        try {
            Path filePath = Paths.get(uploadDir, username, filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public byte[] getFile(String filename, String username) throws IOException {
        Path filePath = Paths.get(uploadDir, username, filename);
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);
        }
        throw new IOException("File not found");
    }

    public boolean fileExists(String filename, String username) {
        Path filePath = Paths.get(uploadDir, username, filename);
        return Files.exists(filePath);
    }
} 