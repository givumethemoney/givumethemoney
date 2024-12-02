package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.repository.ImageRepository;
import com.hey.givumethemoney.repository.S3Repository;

import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public class ImageService {

    @Value("${image.dir}")
    private String fileDir;

    private final ImageRepository imageRepository;
    private final S3Repository s3Repository;

    public ImageService(ImageRepository imageRepository,  S3Repository s3Repository) {
        this.imageRepository = imageRepository;
        this.s3Repository = s3Repository;
    }

    @SuppressWarnings("null")
    public Long saveImages(MultipartFile imageFiles, Long donationId) throws IOException {
        if (imageFiles.isEmpty()) {
            return null;
        }

        String originName = imageFiles.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        String extension = StringUtils.getFilenameExtension(imageFiles.getOriginalFilename());
        if (!(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif"))) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다.");
        }

        String savedName = uuid + "." + extension;
        String imgUrl = s3Repository.uploadFile(imageFiles);
        // thumbPath가 뭐지?
        // String thumbPath = fileDir + "thumb_" + savedName;

        Image image = Image.builder()
                .originName(originName)
                .savedName(savedName)
                .imgUrl(imgUrl)
                // .thumbPath(thumbPath)
                .donationId(donationId)
                .build();

        // Thumbnailator.createThumbnail(new File(imageUrl), new File(thumbPath), 200, 200);

        Image savedImage = imageRepository.save(image);

        return savedImage.getId();
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
        imageRepository.deleteById(id);
    }

    public void deleteImagesByDonationId(Long donationId) {
        List<Image> images = findImagesByDonationId(donationId);
        imageRepository.deleteAll(images);
    }
}
