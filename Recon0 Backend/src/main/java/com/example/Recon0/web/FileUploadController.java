package com.example.Recon0.web;

import com.example.Recon0.dto.FileUploadResponse;
import com.example.Recon0.services.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Tag(name="FileUpload Api", description = "Avatr, Org Logo, Attachment")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload/avatar")
    @Operation(summary = "To upload the user avatar")
    public ResponseEntity<FileUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        FileUploadResponse response = fileUploadService.uploadFile(file, "avatar");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/organization/upload/logo")
    @Operation(summary = "To upload the organization logo")
    public ResponseEntity<FileUploadResponse> uploadOrgLogo(@RequestParam("file") MultipartFile file)throws IOException {
        // Add @PreAuthorize("hasRole('organization')") for security
        FileUploadResponse response = fileUploadService.uploadFile(file, "logo");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/attachment")
    @Operation(summary = "To upload the attachment for report and message")
    public ResponseEntity<FileUploadResponse> uploadAttachment(@RequestParam("file") MultipartFile file)throws IOException {
        FileUploadResponse response = fileUploadService.uploadFile(file, "attachment");
        return ResponseEntity.ok(response);
    }
}

