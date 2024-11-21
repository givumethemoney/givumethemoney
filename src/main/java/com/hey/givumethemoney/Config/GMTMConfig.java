package com.hey.givumethemoney.Config;

import com.hey.givumethemoney.repository.*;
import com.hey.givumethemoney.service.*;

import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GMTMConfig {

    private final DonationRepository donationRepository;
    private final WaitingDonationRepository waitingDonationRepository;
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;
    private final JPAPaymentsRepository paymentsRepository;
    private final JPAInquiryRepository inquiryRepository;
    private final NaverOCRRepository naverOCRRepository;

    @Bean
    public DonationService donationService() {
        return new DonationService(donationRepository, waitingDonationRepository);
    }

    @Bean
    public ImageService imageService() {
        return new ImageService(imageRepository);
    }

    @Bean
    public ProductService productService() {
        return new ProductService(productRepository);
    }

    @Bean
    public ReceiptService receiptService() {
        return new ReceiptService(receiptRepository);
    }

    @Bean
    public PaymentsService paymentsService() {
        return new PaymentsService(paymentsRepository);
    }

    @Bean
    public InquiryService inquiryRService() {
        return new InquiryService(inquiryRepository);
    }

    @Bean
    public NaverOCRService naverOCRService() {
        return new NaverOCRService(naverOCRRepository, imageRepository);
    }

    @Autowired GMTMConfig(DonationRepository donationRepository,
                          WaitingDonationRepository waitingDonationRepository,
                          ImageRepository imageRepository,
                          ProductRepository productRepository,
                          ReceiptRepository receiptRepository,
                          JPAPaymentsRepository paymentsRepository,
                          JPAInquiryRepository inquiryRepository,
                          NaverOCRRepository naverOCRRepository
                          )
    {
        this.donationRepository = donationRepository;
        this.waitingDonationRepository = waitingDonationRepository;
        this.imageRepository = imageRepository;
        this.productRepository = productRepository;
        this.receiptRepository = receiptRepository;
        this.paymentsRepository = paymentsRepository;
        this.inquiryRepository = inquiryRepository;
        this.naverOCRRepository = naverOCRRepository;
    }
}
