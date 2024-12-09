package com.hey.givumethemoney.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.domain.Receipt;

import net.coobird.thumbnailator.Thumbnailator;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.PDPage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;


@Repository
public class S3RepositoryImpl implements S3Repository {

    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // AmazonS3 객체를 생성자 주입 방식으로 받음
    public S3RepositoryImpl(AmazonS3 amazonS3,
                            ImageRepository imageRepository) {
        this.amazonS3 = amazonS3;
        this.imageRepository = imageRepository;
    }

    @Override
    public String uploadImageFile(MultipartFile multipartFile, Image image) {
        String savedName = image.getSavedName();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // S3에 파일 업로드
        try {
            amazonS3.putObject(bucket, savedName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        String imgUrl = amazonS3.getUrl(bucket, savedName).toString();

        return imgUrl;
    }

    
    @Override
    public String uploadReceiptFile(MultipartFile multipartFile, Receipt receipt) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + originalFilename + fileExtension;

        // 1. PDF 파일 처리: PDF 파일일 경우, 이미지를 변환 후 S3에 업로드
        if (fileExtension.equalsIgnoreCase(".pdf")) {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                // PDF 문서 로드
                PDDocument document = PDDocument.load(inputStream);
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                
                // 첫 번째 페이지를 이미지로 변환 (여러 페이지를 처리하려면 반복문을 추가)
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300); // 300 DPI로 렌더링

                // 이미지 파일로 변환하여 ByteArray로 변환
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "JPEG", byteArrayOutputStream); // JPEG 포맷으로 저장

                // ByteArray를 S3에 업로드
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(imageBytes.length);
                metadata.setContentType("image/jpeg");

                String newFileName = UUID.randomUUID().toString() + ".jpg"; // 이미지 파일 이름

                // S3에 업로드
                amazonS3.putObject(bucket, newFileName, new ByteArrayInputStream(imageBytes), metadata);

                String imgUrl = amazonS3.getUrl(bucket, newFileName).toString();
                document.close(); // PDF 문서 닫기

                // 이미지 URL 저장
                receipt.setImageUrl(imgUrl);
                receipt.setSavedName(newFileName);

                return imgUrl;

            } catch (IOException e) {
                throw new RuntimeException("Failed to convert PDF to image", e);
            }
        } else {
            // PDF가 아닌 파일 처리 (기존의 방식)
            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(multipartFile.getSize());
                metadata.setContentType(multipartFile.getContentType());

                amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);

                String imgUrl = amazonS3.getUrl(bucket, fileName).toString();
                receipt.setImageUrl(imgUrl);
                receipt.setSavedName(fileName);

                return imgUrl;
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file to S3", e);
            }
        }
    }

    @Override
    public String createThumbNail(Image image) {

        // 원본 이미지 파일을 S3에서 다운로드하여 InputStream을 받기
        S3ObjectInputStream s3InputStream = downloadFile(image); 
        if (s3InputStream == null) {
            throw new RuntimeException("S3에서 파일을 다운로드할 수 없습니다.");
        }

        // 원본 이미지 InputStream을 BufferedImage로 읽기
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(ImageIO.createImageInputStream(s3InputStream));
        } catch (IOException e) {
            throw new RuntimeException("원본 이미지를 읽을 수 없습니다.", e);
        }
        
        if (originalImage == null) {
            throw new RuntimeException("원본 이미지를 읽을 수 없습니다.");
        }
        
        // 썸네일 파일 이름 생성
        String thumbFileName = "th_" + image.getSavedName(); 
        // 썸네일을 생성하고 업로드하는 부분
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    
        try {
            // Thumbnailator 라이브러리를 사용하여 원본 이미지를 200x200 크기의 썸네일로 생성
            BufferedImage thumbnail = Thumbnailator.createThumbnail(originalImage, 200, 200);
        
            // 썸네일 이미지를 ByteArrayOutputStream에 저장 (파일로 저장하지 않고 메모리 상에서 처리)
            ImageIO.write(thumbnail, "png", byteArrayOutputStream); 
            
            // 썸네일을 ByteArrayOutputStream에서 ByteArray로 변환 후, InputStream으로 변환
            ByteArrayInputStream thumbInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            // S3에 업로드할 메타데이터 생성: 썸네일의 크기 정보 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(byteArrayOutputStream.toByteArray().length);

            // 생성된 썸네일을 S3에 업로드하고, 업로드된 썸네일의 URL을 반환
            String thumbUrl = uploadInputStreamFile(thumbInputStream, thumbFileName, metadata); // S3 업로드 메서드

            System.out.println("s3에 이미지 업로드 완료!!!!\n\n");
            System.out.println("thumbUrl: " + thumbUrl);

            // 썸네일 URL 반환
            return thumbUrl;

        } catch (IOException e) {
            throw new RuntimeException("Failed to create or upload thumbnail", e);
        } finally {
            try {
                if (s3InputStream != null) {
                    s3InputStream.close();  // 리소스 해제
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // S3에서 파일을 다운로드하는 메서드 (입력 받은 URL로 파일을 다운로드하여 InputStream 반환)
    @Override
    public S3ObjectInputStream downloadFile(Image image) {
        // s3Url에서 파일명 추출 (파일 URL에서 경로와 파일명을 분리하는 작업 필요)
        String fileName = image.getSavedName();
        
        // S3에서 파일을 다운로드
        try {
            S3Object s3Object = amazonS3.getObject(bucket, fileName); // S3 객체 가져오기
            return s3Object.getObjectContent(); // S3 객체에서 InputStream 반환
        } catch (AmazonS3Exception e) {
            throw new RuntimeException("S3에서 파일을 다운로드할 수 없습니다.", e);
        }
    }

    @Override
    // S3에 파일 업로드하는 메서드 (입력 받은 InputStream을 S3에 업로드)
    public String uploadInputStreamFile(ByteArrayInputStream thumbInputStream, String fileName, ObjectMetadata metadata) {
        try {
            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, thumbInputStream, metadata));
            // 업로드가 완료되면, 업로드된 파일의 URL 반환
            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (AmazonS3Exception e) {
            throw new RuntimeException("S3에 파일 업로드에 실패했습니다.", e);
        }
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
