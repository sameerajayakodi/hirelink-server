package com.samee.server.service;

import com.samee.server.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    /**
     * Upload a document
     */
    Document uploadDocument(MultipartFile file, String username, String documentType) throws IOException;

    /**
     * Get document by ID
     */
    Document getDocument(String id);

    /**
     * Get all documents for a user
     */
    List<Document> getUserDocuments(String username);

    /**
     * Delete a document
     */
    boolean deleteDocument(String id);

    /**
     * Download a document
     */
    byte[] downloadDocument(String id);
}