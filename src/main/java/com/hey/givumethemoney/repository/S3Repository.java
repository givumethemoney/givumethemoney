package com.hey.givumethemoney.repository;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

public interface S3Repository {
    String uploadFile(MultipartFile file) throws IOException;
    S3ObjectInputStream downloadFile(String fileName);
    void deleteFile(String fileName);
    List<String> listFiles(String prefix);
}
