package com.samee.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Time;

@Data
@NoArgsConstructor
@Entity
@Table(name = "document")
public class Document {
    @Id
    @Column(name = "doc_id")
    private String id;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "doc_path", nullable = false)
    private String docPath;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_time", nullable = false)
    private Time createdTime;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_time")
    private Time updatedTime;

    @Column(name = "created_at")
    private Long createdAt;

    @PrePersist
    protected void onCreate() {
        long currentTimeMillis = System.currentTimeMillis();
        createdDate = new Date(currentTimeMillis);
        createdTime = new Time(currentTimeMillis);
        createdAt = currentTimeMillis;

        // Since doc_path is required but not set in your service
        // Let's make sure it has a value (set it to be the same as file_path if not specified)
        if (docPath == null) {
            docPath = filePath;
        }

        // Generate document_id if not set
        if (documentId == null) {
            documentId = java.util.UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        long currentTimeMillis = System.currentTimeMillis();
        updatedDate = new Date(currentTimeMillis);
        updatedTime = new Time(currentTimeMillis);
    }


}