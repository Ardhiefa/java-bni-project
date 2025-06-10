package com.bni.bni.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {

        try {
            final var uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            final var filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            final var filePath = Paths.get(uploadDir, filename).resolve(filename).normalize();
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            final var fileUrl = "/api/files/" + filename;
            
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "File uploaded successfully",
                    "fileUrl", fileUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "File upload failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{filename..+}")
    public ResponseEntity<Map<String, Object>> getFile(@PathVariable String filename) {
        try {
            final var filePath = Paths.get(uploadDir, filename).resolve(filename).normalize();
            final var resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "File retrieved successfully",
                    "fileUrl", resource.getURL().toString()
            ));
        } catch (MalformedURLException exception) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "File retrieval failed: " + e.getMessage()
            ));
        }
    }
}