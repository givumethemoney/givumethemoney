package com.hey.givumethemoney.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.domain.Receipt;
import com.hey.givumethemoney.domain.ThumbNail;

import net.coobird.thumbnailator.Thumbnailator;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;


@Repository
public class S3RepositoryImpl implements S3Repository {

    private final AmazonS3 amazonS3;
    private final ThumbNailRepository thumbNailRepository;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // AmazonS3 객체를 생성자 주입 방식으로 받음
    public S3RepositoryImpl(AmazonS3 amazonS3,
                            ThumbNailRepository thumbNailRepository, 
                            ImageRepository imageRepository) {
        this.amazonS3 = amazonS3;
        this.thumbNailRepository = thumbNailRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public String uploadImageFile(MultipartFile multipartFile, Image image) {
        System.out.println("uploadFile 시작!!!\n\n");
        
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
        System.out.println("s3에 이미지 업로드 완료!!!!\n\n");
        System.out.println("imgUrl: " + imgUrl);

        return imgUrl;
    }

    @Override
    public String uploadReceiptFile(MultipartFile multipartFile, Receipt receipt) {
        System.out.println("uploadFile 시작!!!\n\n");
        // 1. 파일 이름 추출
        // S3 버킷에 저장될 파일의 이름으로 사용됨
        String originalFilename = multipartFile.getOriginalFilename();

        // 2. 파일 이름에 UUID 추가 (중복 방지)
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + originalFilename + fileExtension;

        // 3. 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        // 파일 크기를 설정
        metadata.setContentLength(multipartFile.getSize());
        // 파일의 MIME 타입을 설정
        metadata.setContentType(multipartFile.getContentType());

        // 4. S3에 파일 업로드
        try {
            amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        String imgUrl = amazonS3.getUrl(bucket, fileName).toString();
        System.out.println("s3에 이미지 업로드 완료!!!!\n\n");
        System.out.println("imgUrl: " + imgUrl);

        // 5. image 정보 저장
        receipt.setImageUrl(imgUrl);
        receipt.setSavedName(fileName);


        return imgUrl;
    }

    @Override
    public String createThumbNail(Image image) {
        System.out.println("createThumbNail 시작!!!\n\n");
        // 2. 원본 이미지의 S3 URL 가져오기
        String imgUrl = image.getImgUrl(); // S3 URL (경로)
        System.out.println("imgUrl: " + imgUrl);
        
        // 3. 원본 이미지 파일을 S3에서 다운로드하여 InputStream을 받기
        InputStream s3InputStream = downloadFile(imgUrl); // S3에서 파일을 다운로드하는 메서드
        if (s3InputStream == null) {
            throw new RuntimeException("S3에서 파일을 다운로드할 수 없습니다.");
        }

        // 4. 원본 이미지 InputStream을 BufferedImage로 읽기
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(s3InputStream);
        } catch (IOException e) {
            throw new RuntimeException("원본 이미지를 읽을 수 없습니다.", e);
        }
        
        if (originalImage == null) {
            throw new RuntimeException("원본 이미지를 읽을 수 없습니다.");
        }
        
        // 5. 썸네일 파일 이름 생성
        String thumbFileName = "th_" + image.getSavedName(); 
        // 썸네일을 생성하고 업로드하는 부분
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    
        try {
            // 6. Thumbnailator로 썸네일 생성 (메모리 상에서)
            // 썸네일 생성: createThumbnail(BufferedImage, int width, int height, OutputStream outputStream)
            BufferedImage thumbnail = Thumbnailator.createThumbnail(originalImage, 200, 200);
        
            // 7. 썸네일을 ByteArrayOutputStream에 저장
            ImageIO.write(thumbnail, "jpg", byteArrayOutputStream);  // JPG 형식으로 저장 (원하는 형식에 맞게 조정)
            byteArrayOutputStream.flush();  // 버퍼 비우기
            
            // 썸네일 생성 완료 후, ByteArrayOutputStream에서 InputStream으로 변환
            ByteArrayInputStream thumbInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            // 7. 메타데이터 생성 (썸네일 파일 업로드를 위한)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(byteArrayOutputStream.toByteArray().length);

            // 8. 썸네일을 S3에 업로드
            String thumbUrl = uploadInputStreamFile(thumbInputStream, thumbFileName, metadata); // S3 업로드 메서드

            System.out.println("s3에 이미지 업로드 완료!!!!\n\n");
            System.out.println("thumbUrl: " + thumbUrl);

            ThumbNail thumbNail = ThumbNail.builder()
            .savedName(thumbFileName)
            .thumbUrl(thumbUrl)
            .donationId(image.getDonationId())
            .imgId(image.getId())
            .build();

            // 5. DB에 Image 저장
            thumbNailRepository.save(thumbNail);

            // 10. 썸네일 URL 반환
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


//     @Override
// public String createThumbNail(Image image) {
//     System.out.println("createThumbNail 시작!!!\n\n");

//     // 2. 원본 이미지의 S3 URL 가져오기
//     String imgUrl = image.getImgUrl(); // S3 URL (경로)
//     System.out.println("imgUrl: " + imgUrl);

//     // 3. 원본 이미지 파일을 S3에서 다운로드하여 InputStream을 받기
//     InputStream s3InputStream = downloadFile(imgUrl); // S3에서 파일을 다운로드하는 메서드
//     if (s3InputStream == null) {
//         throw new RuntimeException("S3에서 파일을 다운로드할 수 없습니다.");
//     }

//     // 4. 원본 이미지 InputStream을 BufferedImage로 읽기
//     BufferedImage originalImage = null;
//     try {
//         originalImage = ImageIO.read(s3InputStream);
//     } catch (IOException e) {
//         throw new RuntimeException("원본 이미지를 읽을 수 없습니다.", e);
//     }

//     if (originalImage == null) {
//         throw new RuntimeException("원본 이미지를 읽을 수 없습니다.");
//     }

//     // 5. 썸네일 파일 이름 생성
//     String thumbFileName = "th_" + image.getSavedName();
    
//     // 6. 로컬에 썸네일 파일 저장
//     File thumbnailFile = new File("/tmp/" + thumbFileName); // 로컬 임시 경로에 저장
//     try {
//         // 7. Thumbnailator로 썸네일 생성 (로컬 파일로 저장)
//         BufferedImage thumbnail = Thumbnailator.createThumbnail(originalImage, 200, 200);
        
//         // 8. 썸네일을 로컬 파일로 저장
//         ImageIO.write(thumbnail, "jpg", thumbnailFile);  // JPG 형식으로 저장 (원하는 형식에 맞게 조정)
//         System.out.println("로컬에 썸네일 저장 완료: " + thumbnailFile.getAbsolutePath());
//     } catch (IOException e) {
//         throw new RuntimeException("썸네일을 로컬에 저장하는 데 실패했습니다.", e);
//     }

//     // 9. 썸네일 파일을 S3에 업로드
//     try {
//         // 10. 메타데이터 생성 (썸네일 파일 업로드를 위한)
//         ObjectMetadata metadata = new ObjectMetadata();
//         metadata.setContentLength(thumbnailFile.length());

//         // 11. 썸네일을 S3에 업로드
//         String thumbUrl = uploadInputStreamFile(new FileInputStream(thumbnailFile), thumbFileName, metadata); // 수정된 부분
//         System.out.println("s3에 이미지 업로드 완료!!!!\n\n");
//         System.out.println("thumbUrl: " + thumbUrl);

//         // 12. 썸네일 정보 DB에 저장
//         ThumbNail thumbNail = ThumbNail.builder()
//                 .savedName(thumbFileName)
//                 .thumbUrl(thumbUrl)
//                 .donationId(image.getDonationId())
//                 .imgId(image.getId())
//                 .build();

//         thumbNailRepository.save(thumbNail);

//         // 13. 썸네일 URL 반환
//         return thumbUrl;

//     } catch (IOException e) {
//         throw new RuntimeException("썸네일을 S3에 업로드하는 데 실패했습니다.", e);
//     } finally {
//         // 14. 리소스 해제
//         try {
//             if (s3InputStream != null) {
//                 s3InputStream.close();  // 리소스 해제
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }



    // @Override
    // public S3ObjectInputStream downloadFile(String fileName) {
    //     S3Object s3Object = amazonS3.getObject(bucket, fileName);
    //     return s3Object.getObjectContent();
    // }

    private String extractFileNameFromUrl(String s3Url) {
        Image image = imageRepository.findByImgUrl(s3Url).get();
        String filename = image.getSavedName();
        return filename;
    }
    
    @Override
    // S3에서 파일을 다운로드하는 메서드 (입력 받은 URL로 파일을 다운로드하여 InputStream 반환)
    public InputStream downloadFile(String s3Url) {
        // s3Url에서 파일명 추출 (파일 URL에서 경로와 파일명을 분리하는 작업 필요)
        String fileName = extractFileNameFromUrl(s3Url);
        System.out.println("[InputStream downloadFile] fileName: " + fileName);
        
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
