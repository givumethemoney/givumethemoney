package com.hey.givumethemoney.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.hey.givumethemoney.domain.Receipt;
import com.hey.givumethemoney.repository.ReceiptRepository;
import com.hey.givumethemoney.repository.S3Repository;

@Transactional
@Service
public class ReceiptService {


    private final ReceiptRepository receiptRepository;
    private final S3Repository s3Repository;

    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository, S3Repository s3Repository) {
        this.receiptRepository = receiptRepository;
        this.s3Repository = s3Repository;
    }

    @SuppressWarnings("null")
    public Long saveReceipts(MultipartFile receiptFiles, Long donationId) throws IOException {
        if (receiptFiles.isEmpty()) {
            return null;
        }

        String originName = receiptFiles.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        String extension = StringUtils.getFilenameExtension(receiptFiles.getOriginalFilename());
        if (!(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("pdf") ||extension.equals("gif"))) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다.");
        }

        Receipt receipt = new Receipt();
        s3Repository.uploadReceiptFile(receiptFiles, receipt);

        receipt.setOriginName(originName);
        receipt.setDonationId(donationId);

        Receipt savedReceipt = receiptRepository.save(receipt);

        return savedReceipt.getId();
    }

    public Optional<Receipt> findReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    public List<Receipt> findByDonationId(Long donationId) {
        List<Receipt> result = receiptRepository.findByDonationId(donationId);
        System.out.println("receiptservice - result: " + result);

        return result;
    }
}
