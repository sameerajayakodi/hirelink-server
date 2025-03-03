package com.samee.server.controller;

import com.samee.server.entity.Document;
import com.samee.server.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/documents")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Document document = documentService.uploadDocument(file, username, documentType);
            return new ResponseEntity<>(document, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload document: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/my-documents")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Document>> getMyDocuments(Authentication authentication) {
        String username = authentication.getName();
        List<Document> documents = documentService.getUserDocuments(username);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDocument(@PathVariable String id) {
        try {
            Document document = documentService.getDocument(id);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteDocument(@PathVariable String id) {
        try {
            boolean deleted = documentService.deleteDocument(id);
            if (deleted) {
                return ResponseEntity.ok("Document deleted successfully");
            } else {
                return new ResponseEntity<>("Failed to delete document", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String id) {
        try {
            Document document = documentService.getDocument(id);
            byte[] data = documentService.downloadDocument(id);

            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(document.getFileType()))
                    .contentLength(document.getFileSize())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}