package com.samee.server.controller;

import com.samee.server.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@RestController
@RequestMapping("api/v1/documents")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true",
        allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class DocumentController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String filename,
            @RequestParam String token) {

        // Set authentication from token
        if (token != null && !token.isEmpty()) {
            // This is a simplified example - use your existing JWT validator
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "company-user", null,
                    Collections.singletonList(new SimpleGrantedAuthority("COMPANY"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        Resource file = fileStorageService.loadFileAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        // Get content type
        String contentType = "application/octet-stream";
        // ... content type detection code ...

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}