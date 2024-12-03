package com.hey.givumethemoney.repository;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.domain.Receipt;

public interface S3Repository {
    String uploadImageFile(MultipartFile file, Image image) throws IOException;
    String uploadReceiptFile(MultipartFile file, Receipt receipt) throws IOException;
    String createThumbNail(Image image);
    InputStream downloadFile(String fileName);
    void deleteFile(String fileName);
    List<String> listFiles(String prefix);
    String uploadInputStreamFile(ByteArrayInputStream thumbInputStream, String thumbFileName, ObjectMetadata metadata);
}
