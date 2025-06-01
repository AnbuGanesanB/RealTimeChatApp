package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.model.files.SharedFile;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.FileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepo fileRepo;

    @Value("${file.uploadDirectory}")
    private String uploadDirectory;

    @Value("${file.displayPictureDirectory}")
    private String displayPictureDirectory;

    public void processIncomingFiles(List<MultipartFile> files, Message message) {

        for (MultipartFile file : files) {
            try {
                String originalFilename = file.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

                Path uploadPath = Paths.get(uploadDirectory).resolve(uniqueFileName);
                System.out.println("Upload Path: "+uploadPath);

                Files.createDirectories(uploadPath.getParent()); // ensure folder exists
                file.transferTo(uploadPath.toFile());

                SharedFile newSharedFile = new SharedFile();
                newSharedFile.setOriginalFileName(originalFilename);
                newSharedFile.setUniqueFileName(uniqueFileName);
                newSharedFile.setFilePath(uploadPath.toString());

                newSharedFile.setLinkedMessage(message);
                message.getSharedFiles().add(newSharedFile);
                fileRepo.save(newSharedFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public User setProfilePicture(MultipartFile profilePic, User user){
        try {
            String originalFilename = profilePic.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            Path uploadPath = Paths.get(displayPictureDirectory).resolve(uniqueFileName);
            System.out.println("Upload Path: "+uploadPath);

            Files.createDirectories(uploadPath.getParent());
            profilePic.transferTo(uploadPath.toFile());

            user.setDpPath(uniqueFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }
}
