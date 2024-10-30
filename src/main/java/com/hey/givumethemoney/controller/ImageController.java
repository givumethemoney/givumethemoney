package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.service.DonationService;
import com.hey.givumethemoney.service.ImageService;
import com.hey.givumethemoney.service.ProductService;
import com.hey.givumethemoney.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
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

    @GetMapping("/images/{id}")
    @ResponseBody
    public UrlResource showImage(@PathVariable Long id, Model model) throws IOException {
        Optional<Image> image = imageService.findImageById(id);
        if (image.isPresent()) {
            return new UrlResource("file:" + image.get().getSavedPath());

        }
        else {
            return null;
        }
    }

    @GetMapping("/thumbs/{id}")
    @ResponseBody
    public UrlResource showThumb(@PathVariable Long id, Model model) throws IOException {
        Optional<Image> image = imageService.findImageById(id);
        if (image.isPresent()) {
            return new UrlResource("file:" + image.get().getThumbPath());
        }
        else {
            return null;
        }
    }
}
