package com.samee.server.service.impl;

import com.samee.server.entity.Document;
import com.samee.server.entity.User;
import com.samee.server.repo.DocumentRepo;
import com.samee.server.repo.UserRepo;
import com.samee.server.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepo documentRepo;
    private final UserRepo userRepo;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Autowired
    public DocumentServiceImpl(DocumentRepo documentRepo, UserRepo userRepo) {
        this.documentRepo = documentRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public Document uploadDocument(MultipartFile file, String username, String documentType) throws IOException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Create upload directory if it doesn't exist
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Normalize the file name
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Check if the file's name contains invalid characters
        if (originalFileName.contains("..")) {
            throw new RuntimeException("Filename contains invalid path sequence: " + originalFileName);
        }

        // Generate a unique file name to prevent name collisions
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID() + "." + fileExtension;

        // Copy file to the target location
        Path targetLocation = Paths.get(uploadDir).resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Create document entity
        Document document = new Document();
        document.setId(UUID.randomUUID().toString());
        document.setDocumentId(UUID.randomUUID().toString());
        document.setUser(user);
        document.setFileName(originalFileName);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setFilePath(targetLocation.toString());
        document.setDocPath(targetLocation.toString()); // Set doc_path same as file_path
        document.setDocumentType(documentType);

        return documentRepo.save(document);
    }

    @Override
    public Document getDocument(String id) {
        Optional<Document> documentOpt = documentRepo.findById(id);
        if (documentOpt.isEmpty()) {
            throw new RuntimeException("Document not found with ID: " + id);
        }
        return documentOpt.get();
    }

    @Override
    public List<Document> getUserDocuments(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return documentRepo.findAllByUser(user);
    }

    @Override
    @Transactional
    public boolean deleteDocument(String id) {
        Optional<Document> documentOpt = documentRepo.findById(id);
        if (documentOpt.isEmpty()) {
            throw new RuntimeException("Document not found with ID: " + id);
        }

        Document document = documentOpt.get();

        // Delete the physical file
        try {
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log the error but continue deleting the database record
            System.err.println("Error deleting file: " + e.getMessage());
        }

        // Delete the database record
        documentRepo.delete(document);
        return true;
    }

    @Override
    public byte[] downloadDocument(String id) {
        Optional<Document> documentOpt = documentRepo.findById(id);
        if (documentOpt.isEmpty()) {
            throw new RuntimeException("Document not found with ID: " + id);
        }

        Document document = documentOpt.get();

        try {
            Path filePath = Paths.get(document.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return ""; // Empty extension
        }
    }
}