package com.samee.server.service.impl;

import com.samee.server.entity.Document;
import com.samee.server.entity.User;
import com.samee.server.repo.DocumentRepo;
import com.samee.server.repo.UserRepo;
import com.samee.server.service.DocumentService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Time;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final Path rootLocation;
    private final DocumentRepo documentRepo;
    private final UserRepo userRepo;

    @Autowired
    public DocumentServiceImpl(DocumentRepo documentRepo, UserRepo userRepo) {
        this.documentRepo = documentRepo;
        this.userRepo = userRepo;
        this.rootLocation = Paths.get("src/main/resources/uploads");
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String saveFile(MultipartFile file, String username) throws BadRequestException {
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("Failed to store empty file.");
            }

            User user = userRepo.findByUsername(username);
            if (user == null) {
                throw new BadRequestException("User not found: " + username);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destinationFile = this.rootLocation.resolve(fileName).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new BadRequestException("Cannot store file outside the current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Generate URL for accessing the file
            String fileUrl = "/api/v1/documents/files/" + fileName;

            // Save file info to database
            Document document = new Document();
            document.setDocPath(fileUrl);
            document.setCreatedDate(new Date(System.currentTimeMillis()));
            document.setCreatedTime(new Time(System.currentTimeMillis()));
            document.setUser(user);

            documentRepo.save(document);

            return fileUrl;
        } catch (Exception e) {
            throw new BadRequestException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource getFileByName(String filename) throws BadRequestException {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new BadRequestException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new BadRequestException("Could not read file: " + filename, e);
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Stream<Path> loadAll() throws BadRequestException {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new BadRequestException("Failed to read stored files", e);
        }

    }

    @Override
    public void delete(String filename) {
        Path filePath = Paths.get(rootLocation.toString(), filename);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteAll() throws IOException {
        Path dirPath = Paths.get(rootLocation.toUri());

        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    });
        }
    }

}
