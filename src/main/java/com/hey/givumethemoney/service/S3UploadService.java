package com.hey.givumethemoney.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3UploadService {

     private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        // 1. 파일 이름 추출
        // S3 버킷에 저장될 파일의 이름으로 사용됨
        String originalFilename = multipartFile.getOriginalFilename();

        // 2. 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        // 파일 크기를 설정
        metadata.setContentLength(multipartFile.getSize());
        // 파일의 MIME 타입을 설정
        metadata.setContentType(multipartFile.getContentType());

        // 3. S3에 파일 업로드
        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);

        // 4. 파일 URL 생성 및 반환
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }
}