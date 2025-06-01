package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.files.SharedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<SharedFile, Integer> {

    Optional<SharedFile> findByUniqueFileName(String uniqueFileName);
}
