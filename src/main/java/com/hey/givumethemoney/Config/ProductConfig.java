package com.hey.givumethemoney.Config;

import com.hey.givumethemoney.repository.ProductRepository;
import com.hey.givumethemoney.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {
    private final ProductRepository productRepository;

    @Autowired
    public ProductConfig(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Bean
    public ProductService productService() {
        return new ProductService(productRepository);
    }
}
