package com.samee.server.repo;


import com.samee.server.entity.Document;
import com.samee.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentRepo extends JpaRepository<Document, String> {
    List<Document> findByUserId(String userId);
    List<Document> findAllByUser(User user);

}

