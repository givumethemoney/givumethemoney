package com.hey.givumethemoney.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public class S3RepositoryImpl implements S3Repository {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // AmazonS3 객체를 생성자 주입 방식으로 받음
    public S3RepositoryImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
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
        try {
            amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        // 4. 파일 URL 생성 및 반환
        return amazonS3.getUrl(bucket, originalFilename).toString();
     }

     @Override
     public S3ObjectInputStream downloadFile(String fileName) {
         S3Object s3Object = amazonS3.getObject(bucket, fileName);
         return s3Object.getObjectContent();
     }
     

    @Override
    public void deleteFile(String fileName) {
        amazonS3.deleteObject(bucket, fileName);
    }

    @Override
    public List<String> listFiles(String prefix) {
        ObjectListing objectListing = amazonS3.listObjects(bucket, prefix);
        return objectListing.getObjectSummaries()
                            .stream()
                            .map(S3ObjectSummary::getKey)
                            .collect(Collectors.toList());
    }
}
