package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Receipt;
import com.hey.givumethemoney.repository.NaverOCRRepository;
import com.hey.givumethemoney.repository.ReceiptRepository;
import com.hey.givumethemoney.repository.S3Repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
@Service
public class ReceiptService {


    ReceiptRepository receiptRepository;
    S3Repository s3Repository;

    public ReceiptService(ReceiptRepository receiptRepository, S3Repository s3Repository) {
        this.receiptRepository = receiptRepository;
        this.s3Repository = s3Repository;
    }

    public Long saveReceipts(MultipartFile receiptFiles, Long donationId) throws IOException {
        if (receiptFiles.isEmpty()) {
            return null;
        }

        String originName = receiptFiles.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        String extension = StringUtils.getFilenameExtension(receiptFiles.getOriginalFilename());
        if (!(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif"))) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다.");
        }

        String savedName = uuid + "." + extension;
        String imageUrl = s3Repository.uploadFile(receiptFiles);

        Receipt receipt = Receipt.builder()
                .originName(originName)
                .savedName(savedName)
                .donationId(donationId)
                .imageUrl(imageUrl)
                .build();

        // File folder = new File(fileDir);
        // if (!folder.exists()) {
        //     try {
        //         folder.mkdir();
        //     }
        //     catch (Exception e) {
        //         e.getStackTrace();
        //     }
        // }
        Receipt savedReceipt = receiptRepository.save(receipt);

        return savedReceipt.getId();
    }

    public Optional<Receipt> findReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    public List<Receipt> findByDonationId(Long donationId) {
        List<Receipt> result = receiptRepository.findByDonationId(donationId);

        return result;
    }
}
