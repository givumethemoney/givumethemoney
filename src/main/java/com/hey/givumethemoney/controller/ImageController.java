package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.service.DonationService;
import com.hey.givumethemoney.service.ImageService;
import com.hey.givumethemoney.service.ProductService;
import com.hey.givumethemoney.service.ReceiptService;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Optional;

@Controller
public class ImageController {

    ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // donationDetail.html 에서 쓰이는데, s3에서 불러와서 이제 이 함수 필요없을듯
    @GetMapping("/images/{id}")
    @ResponseBody
    public ResponseEntity<String> showImage(@PathVariable Long id, Model model) throws IOException {
        Optional<Image> image = imageService.findImageById(id);
        if (image.isPresent()) {
        // S3에 저장된 URL 반환
        String imageUrl = image.get().getImgUrl();
        return ResponseEntity.ok(imageUrl);
    } else {
        // 이미지가 없을 경우 404 에러 반환
        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Image not found");
    }
    }

    // @GetMapping("/thumbs/{id}")
    // @ResponseBody
    // public UrlResource showThumb(@PathVariable Long id, Model model) throws IOException {
    //     Optional<Image> image = imageService.findImageById(id);
    //     if (image.isPresent()) {
    //         return new UrlResource("file:" + image.get().getThumbPath());
    //     }
    //     else {
    //         return null;
    //     }
    // }
}
