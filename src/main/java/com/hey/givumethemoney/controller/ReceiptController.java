package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.OCRResult;
import com.hey.givumethemoney.domain.Receipt;
import com.hey.givumethemoney.service.NaverOCRService;
import com.hey.givumethemoney.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class ReceiptController {

    private final ReceiptService receiptService;
    private final NaverOCRService naverOCRService;

    @Autowired
    public ReceiptController(ReceiptService receiptService, NaverOCRService naverOCRService) {
        this.receiptService = receiptService;
        this.naverOCRService = naverOCRService;
    }

    @GetMapping("/receiptPopup")
    public String receipt(@RequestParam(value = "id", required = false) Long donationId, Model model) throws IOException {
        model.addAttribute("donationId", donationId);
        return "getReceipt";
    }

    @PostMapping("/receipt/submit")
    public String submitReceipt(@RequestParam(value = "receipt", required = false) List<MultipartFile> files,
                                @RequestParam(value = "donationId", required = false) Long donationId,
                                Model model) throws IOException {
        for (MultipartFile file : files) {
            if (receiptService.saveReceipts(file, donationId) == null) {
                // 이미지 확장자 외 파일 오류
                System.out.println("다른 확장자");

                return null;
            }
        }
        model.addAttribute("donationId", donationId);

        return "close";
    }

    @GetMapping("receiptList/{donationId}")
    public String receiptList(@PathVariable Long donationId, Model model) throws IOException {
        List<Receipt> receipts = receiptService.findByDonationId(donationId);
        List<OCRResult> ocrResults = new ArrayList<>();
        
        // OCR 서비스에서 각 영수증에 대한 OCR 결과를 가져오기
        for (Receipt r : receipts) {
            List<OCRResult> result = naverOCRService.sendImageToOCR(Collections.singletonList(r));
            ocrResults.addAll(result);
        }
        
        model.addAttribute("receipts", receipts);
        model.addAttribute("donationId", donationId);
        model.addAttribute("ocrResults", ocrResults); // ocrResults 객체 배열 전달

        return "donationReceipt";
    }


    @GetMapping("/receipts/{id}")
    @ResponseBody
    public UrlResource showReceipt(@PathVariable Long id, Model model) throws IOException {
        Optional<Receipt> receipt = receiptService.findReceiptById(id);
        if (receipt.isPresent()) {
            String imageUrl = receipt.get().getImageUrl(); // S3에서 저장된 URL
            return new UrlResource(imageUrl);
        }
        else {
            return null;
        }
    }
}
