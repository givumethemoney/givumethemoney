package com.hey.givumethemoney.Config;

import com.hey.givumethemoney.repository.*;
import com.hey.givumethemoney.service.*;


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
    private final S3Repository s3Repository;
    private final NicknameDonationRepository nicknameDonationRepository;

    @Bean
    public DonationService donationService() {
        return new DonationService(donationRepository, waitingDonationRepository, nicknameDonationRepository);
    }

    @Bean
    public ImageService imageService() {
        return new ImageService(imageRepository, s3Repository);
    }

    @Bean
    public ProductService productService() {
        return new ProductService(productRepository);
    }

    @Bean
    public S3Service s3Service() {
        return new S3Service(s3Repository);
    }

    @Bean
    public ReceiptService receiptService() {
        return new ReceiptService(receiptRepository, s3Repository);
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
        return new NaverOCRService(naverOCRRepository, receiptRepository);
    }

   GMTMConfig(DonationRepository donationRepository,
                          WaitingDonationRepository waitingDonationRepository,
                          ImageRepository imageRepository,
                          ProductRepository productRepository,
                          ReceiptRepository receiptRepository,
                          JPAPaymentsRepository paymentsRepository,
                          JPAInquiryRepository inquiryRepository,
                          NaverOCRRepository naverOCRRepository,
                          S3Repository s3Repository,
                          NicknameDonationRepository nicknameDonationRepository
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
        this.s3Repository= s3Repository;
        this.nicknameDonationRepository = nicknameDonationRepository;
    }
}
