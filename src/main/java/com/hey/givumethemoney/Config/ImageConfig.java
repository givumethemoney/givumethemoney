package com.hey.givumethemoney.Config;

import com.hey.givumethemoney.repository.ImageRepository;
import com.hey.givumethemoney.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageConfig {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageConfig(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Bean
    public ImageService imageService() {
        return new ImageService(imageRepository);
    }
}
