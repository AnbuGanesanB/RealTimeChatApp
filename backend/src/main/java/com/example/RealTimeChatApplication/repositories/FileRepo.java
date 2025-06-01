package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.files.SharedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<SharedFile, Integer> {

}
