package com.realfreelancer.controller;

import com.realfreelancer.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:3000")
public class FileServingController {

    @Autowired
    private FileService fileService;

    @GetMapping("/{username}/{filename}")
    public ResponseEntity<ByteArrayResource> serveFile(
            @PathVariable String username,
            @PathVariable String filename
    ) {
        try {
            byte[] fileContent = fileService.getFile(filename, username);
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            // Determine content type based on file extension
            String contentType = getContentType(filename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(fileContent.length)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String getContentType(String filename) {
        String extension = "";
        if (filename.contains(".")) {
            extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        }

        return switch (extension) {
            case ".pdf" -> "application/pdf";
            case ".doc" -> "application/msword";
            case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".txt" -> "text/plain";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".zip" -> "application/zip";
            case ".rar" -> "application/x-rar-compressed";
            default -> "application/octet-stream";
        };
    }
} 