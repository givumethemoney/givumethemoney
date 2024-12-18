package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.service.S3Service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class S3Controller {

    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // @PostMapping("/upload")
    // public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
    //     String fileUrl = s3Service.uploadFile(file);
    //     return ResponseEntity.ok(fileUrl);
    // }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        s3Service.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(@RequestParam(value = "prefix", required = false) String prefix) {
        List<String> files = s3Service.listFiles(prefix == null ? "" : prefix);
        return ResponseEntity.ok(files);
    }
}
