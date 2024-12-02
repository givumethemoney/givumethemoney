package com.hey.givumethemoney.service;

import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.hey.givumethemoney.repository.S3Repository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Repository s3Repository;

    /**
     * 파일 업로드
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        return s3Repository.uploadFile(multipartFile);    
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fileName) {
        s3Repository.deleteFile(fileName);
    }

    /**
     * S3 버킷의 모든 파일 목록 조회
     */
    public List<String> listFiles(String prefix) {
        return s3Repository.listFiles(prefix);   
    }

    /**
     * S3에서 파일 다운로드
     */
    public S3ObjectInputStream downloadFile(String fileName) {
        return s3Repository.downloadFile(fileName);
    }
}




