package com.hey.givumethemoney.Config;

import com.hey.givumethemoney.domain.Receipt;
import com.hey.givumethemoney.repository.ReceiptRepository;
import com.hey.givumethemoney.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReceiptConfig {
    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptConfig(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Bean
    public ReceiptService receiptService() {
        return new ReceiptService(receiptRepository);
    }
}
