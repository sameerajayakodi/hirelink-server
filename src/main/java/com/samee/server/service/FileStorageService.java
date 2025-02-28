package com.samee.server.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for handling file storage operations
 */
public interface FileStorageService {

    /**
     * Store a file in the file system
     *
     * @param file The file to store
     * @return The filename of the stored file
     */
    String storeFile(MultipartFile file);

    /**
     * Load a file as a resource
     *
     * @param fileName The name of the file to load
     * @return The file resource
     */
    Resource loadFileAsResource(String fileName);
}
