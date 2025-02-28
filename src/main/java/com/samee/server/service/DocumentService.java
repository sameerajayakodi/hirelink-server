package com.samee.server.service;

import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface DocumentService {
    String saveFile(MultipartFile file,String username) throws BadRequestException;
    Resource getFileByName(String filename) throws BadRequestException;
    Path load(String filename);
    Stream<Path> loadAll() throws BadRequestException;
    void delete(String filename);
    void deleteAll() throws IOException;
}
