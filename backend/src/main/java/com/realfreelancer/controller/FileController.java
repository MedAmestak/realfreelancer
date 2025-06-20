package com.realfreelancer.controller;

import com.realfreelancer.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    @Autowired
    private FileService fileService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {
        ".pdf", ".doc", ".docx", ".txt", ".jpg", ".jpeg", ".png", ".gif", ".zip", ".rar"
    };

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File cannot be empty");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body("File size exceeds 5MB limit");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid filename");
            }

            // Check file extension
            boolean validExtension = false;
            for (String extension : ALLOWED_EXTENSIONS) {
                if (originalFilename.toLowerCase().endsWith(extension)) {
                    validExtension = true;
                    break;
                }
            }

            if (!validExtension) {
                return ResponseEntity.badRequest().body("File type not allowed. Allowed types: " + 
                    String.join(", ", ALLOWED_EXTENSIONS));
            }

            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Upload file
            String fileUrl = fileService.uploadFile(file, username);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            response.put("fileUrl", fileUrl);
            response.put("originalName", originalFilename);
            response.put("size", file.getSize());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading file: File upload failed");
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            boolean deleted = fileService.deleteFile(filename, username);
            if (deleted) {
                return ResponseEntity.ok("File deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting file: File not found");
        }
    }

    @GetMapping("/allowed-types")
    public ResponseEntity<?> getAllowedFileTypes() {
        Map<String, Object> response = new HashMap<>();
        response.put("allowedExtensions", ALLOWED_EXTENSIONS);
        response.put("maxSizeMB", MAX_FILE_SIZE / (1024 * 1024));
        return ResponseEntity.ok(response);
    }
} 