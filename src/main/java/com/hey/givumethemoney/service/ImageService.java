package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.repository.ImageRepository;
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

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Long saveImages(MultipartFile imageFiles, Long donationId) throws IOException {
        if (imageFiles.isEmpty()) {
            return null;
        }

        String originName = imageFiles.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        String extension = StringUtils.getFilenameExtension(imageFiles.getOriginalFilename());
        if (!(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif"))) {
            return null;
        }

        String savedName = uuid + "." + extension;
        String savedPath = fileDir + savedName;

        Image image = Image.builder()
                .originName(originName)
                .savedName(savedName)
                .savedPath(savedPath)
                .donationId(donationId)
                .build();

        imageFiles.transferTo(new File(savedPath));

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

    public void deleteImagesById(Long id) {
        List<Image> images = findImagesByDonationId(id);
        imageRepository.deleteAll(images);
    }
}
