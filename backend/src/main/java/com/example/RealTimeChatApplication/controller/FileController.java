package com.example.RealTimeChatApplication.controller;

import com.example.RealTimeChatApplication.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5175", exposedHeaders = "ContactId")
public class FileController {

    private final FileService fileService;

    @Value("${file.uploadDirectory}")
    private String uploadDirectory;

    @Value("${file.displayPictureDirectory}")
    private String displayPictureDirectory;

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename){

        try{
            System.out.println("Incoming req. file name:"+filename);
            Path fileStorageLocation = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            System.out.println("fileStorageLocation:"+fileStorageLocation);
            Path targetPath = fileStorageLocation.resolve(filename).normalize();
            System.out.println("targetPath"+targetPath);

            Resource resource = new UrlResource(targetPath.toUri());
            System.out.println("Resource file name:"+resource.getFilename());

            // Check that the resolved path is still under uploadDir
            // If not - reject the request
            if (!targetPath.startsWith(fileStorageLocation)) {
                return ResponseEntity.badRequest().build();
            }

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(targetPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/dp/{filename:.+}")
    public ResponseEntity<Resource> getProfilePic(@PathVariable String filename){

        try{
            System.out.println("Incoming req. file name:"+filename);
            Path fileStorageLocation = Paths.get(displayPictureDirectory).toAbsolutePath().normalize();
            System.out.println("fileStorageLocation:"+fileStorageLocation);
            Path targetPath = fileStorageLocation.resolve(filename).normalize();
            System.out.println("targetPath"+targetPath);

            Resource resource = new UrlResource(targetPath.toUri());
            System.out.println("Resource file name:"+resource.getFilename());

            // Check that the resolved path is still under uploadDir
            // If not - reject the request
            if (!targetPath.startsWith(fileStorageLocation)) {
                return ResponseEntity.badRequest().build();
            }

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(targetPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
