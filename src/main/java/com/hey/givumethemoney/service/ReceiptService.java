package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Receipt;
import com.hey.givumethemoney.repository.ReceiptRepository;

import org.springframework.beans.factory.annotation.Value;
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
public class ReceiptService {

    @Value("${receipt.dir}")
    private String fileDir;

    private final ReceiptRepository receiptRepository;
    private final S3UploadService s3UploadService;

    public ReceiptService(ReceiptRepository receiptRepository, S3UploadService s3UploadService) {
        this.receiptRepository = receiptRepository;
        this.s3UploadService = s3UploadService;
    }

    public Long saveReceipts(MultipartFile receiptFiles, Long donationId) throws IOException {
        if (receiptFiles.isEmpty()) {
            return null;
        }

        String originName = receiptFiles.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        String extension = StringUtils.getFilenameExtension(receiptFiles.getOriginalFilename());
        if (!(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif"))) {
            return null;
        }

        String savedName = uuid + "." + extension;
        String imageUrl = s3UploadService.saveFile(receiptFiles);

        Receipt receipt = Receipt.builder()
                .originName(originName)
                .savedName(savedName)
                .donationId(donationId)
                .imageUrl(imageUrl)
                .build();

        File folder = new File(fileDir);
        if (!folder.exists()) {
            try {
                folder.mkdir();
            }
            catch (Exception e) {
                e.getStackTrace();
            }
        }
        Receipt savedReceipt = receiptRepository.save(receipt);

        return savedReceipt.getId();
    }

    public Optional<Receipt> findReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    public List<Receipt> findReceiptsByDonationId(Long donationId) {
        List<Receipt> result = new ArrayList<>();

        for (Receipt receipt : receiptRepository.findAll()) {
            if (receipt.getDonationId().equals(donationId)) {
                result.add(receipt);
            }
        }

        return result;
    }
}
