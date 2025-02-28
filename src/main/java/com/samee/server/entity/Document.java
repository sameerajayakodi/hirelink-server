package com.samee.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@RequiredArgsConstructor
@Data
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "doc_id")
    private String id;
    @Column(nullable = false)
    private String docPath;
    @Column(nullable = false)
    private Date createdDate;
    @Column(nullable = false)
    private Time createdTime;
    private Date updatedDate;
    private Time updatedTime;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
