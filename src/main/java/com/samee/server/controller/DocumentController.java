package com.samee.server.controller;

import com.samee.server.service.DocumentService;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> addFiles(@RequestParam("file") MultipartFile file, @RequestParam("userName") String username) {
        try {
            // Make sure username parameter matches the service method parameter
            return new ResponseEntity<>(documentService.saveFile(file, username), HttpStatus.CREATED);
        } catch (BadRequestException e) {
            e.printStackTrace(); // Add this for debugging
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file;
        try {
            file = documentService.getFileByName(filename);
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/get-all")
    @ResponseBody
    public ResponseEntity<?> listUploadedFiles() {
        try {
            List<String> fileUrls = documentService.loadAll().map(
                            path -> MvcUriComponentsBuilder.fromMethodName(DocumentController.class,
                                    "serveFile", path.getFileName().toString()).build().toUri().toString())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(fileUrls);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{filename:.+}")
    @ResponseBody
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            documentService.delete(filename);
            return ResponseEntity.ok("File deleted successfully: " + filename);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("File not found: " + filename);
        }
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<String> deleteAll() {
        try {
            documentService.deleteAll();
            return ResponseEntity.ok("All files deleted successfully");
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}