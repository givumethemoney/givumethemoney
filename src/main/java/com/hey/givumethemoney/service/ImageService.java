package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.repository.ImageRepository;
import com.hey.givumethemoney.repository.S3RepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3RepositoryImpl s3RepositoryImpl;

    @Autowired
    public ImageService(ImageRepository imageRepository,  S3RepositoryImpl s3RepositoryImpl) {
        this.imageRepository = imageRepository;
        this.s3RepositoryImpl = s3RepositoryImpl;
    }

    @SuppressWarnings("null")
    public Image saveImage(MultipartFile file, Long donationId) throws IOException {

        Image image = new Image();

        // 1. 파일 이름 및 확장자 추출
        String originName = file.getOriginalFilename();
        image.setOriginName(originName);
        image.setDonationId(donationId);

        if (originName == null) {
            throw new IllegalArgumentException("파일 이름이 없습니다.");
        }

        String extension = StringUtils.getFilenameExtension(originName);
        if (!(extension.equalsIgnoreCase("jpg") || 
            extension.equalsIgnoreCase("jpeg") || 
            extension.equalsIgnoreCase("png") ||
            extension.equalsIgnoreCase("gif"))) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + extension);
        }

        String uuid = UUID.randomUUID().toString();
        String savedName = uuid + "." + extension;
        image.setSavedName(savedName);

        // S3에 파일 업로드 및 URL 반환
        System.out.println("s3Repository에서 uploadFile 호출");
        image.setImgUrl(s3RepositoryImpl.uploadImageFile(file, image));
        image.setThumbUrl(s3RepositoryImpl.createThumbNail(image));

        // DB에 Image 저장
        Image savedImage = imageRepository.save(image);

        return savedImage;
    }

    public Optional<Image> findImageById(Long id) {
        return imageRepository.findById(id);
    }

    public List<Image> findImagesByDonationId(Long donationId) {
        List<Image> result = new ArrayList<>();

        for (Image image : imageRepository.findAll()) {
            if (image.getDonationId().equals(donationId)) {
                result.add(image);
            }
        }

        return result;
    }

    public void deleteImageById(Long id) {
        Optional<Image> image = findImageById(id);
        if (image.isPresent()) {
            s3RepositoryImpl.deleteFile(image.get().getSavedName());
            s3RepositoryImpl.deleteFile("th_" + image.get().getSavedName());
            imageRepository.deleteById(id);
        }
    }

    public void deleteImagesByDonationId(Long donationId) {
        List<Image> images = findImagesByDonationId(donationId);
        for (Image image : images) {
            s3RepositoryImpl.deleteFile(image.getSavedName());
        }
        imageRepository.deleteAll(images);
    }
}
