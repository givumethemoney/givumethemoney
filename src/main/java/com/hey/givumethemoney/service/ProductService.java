package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Product;
import com.hey.givumethemoney.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void saveProducts(List<Product> productList) {
        for (Product p : productList) {
            productRepository.save(p);
        }
    }

    public List<Product> getProductsByDonationId(Long id) {
        List<Product> productList = productRepository.findAll();

        List<Product> result = new ArrayList<>();
        for (Product p : productList) {
            if (p.getDonationId().equals(id)) {
                result.add(p);
            }
        }

        return result;
    }

    public void deleteProductsByDonationId(Long id) {
        List<Product> productList = getProductsByDonationId(id);
        productRepository.deleteAll(productList);
    }
}
