package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Receipt;
import com.hey.givumethemoney.service.ImageService;
import com.hey.givumethemoney.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class ReceiptController {

    ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/receiptPopup")
    public String receipt(@RequestParam(value = "id", required = false) Long donationId, Model model) throws IOException {
        model.addAttribute("donationId", donationId);
        return "getReceipt";
    }

    @PostMapping("/receipt/submit")
    public String submitReceipt(@RequestParam(value = "receipt", required = false) List<MultipartFile> files, @RequestParam(value = "donationId", required = false) Long donationId) throws IOException {
        for (MultipartFile file : files) {
            if (receiptService.saveReceipts(file, donationId) == null) {
                // 이미지 확장자 외 파일 오류
                System.out.println("다른 확장자");

                return null;
            }
        }

        return "redirect:/receipt/closePopup?donationId=" + donationId;
    }

    @GetMapping("/receipt/closePopup")
    public String closePopup(@RequestParam Long donationId, Model model) {
        model.addAttribute("donationId", donationId);
        return "closePopup";  // JavaScript를 포함한 HTML 페이지로 리턴
    }

    @GetMapping("receiptList/{donationId}")
    public String receiptList(@PathVariable Long donationId, Model model) throws IOException {
        List<Receipt> receipts = receiptService.findReceiptsByDonationId(donationId);

        model.addAttribute("receipts", receipts);

        return "donationReceipt";
    }


    @GetMapping("/receipts/{id}")
    @ResponseBody
    public UrlResource showReceipt(@PathVariable Long id, Model model) throws IOException {
        Optional<Receipt> receipt = receiptService.findReceiptById(id);
        if (receipt.isPresent()) {
            return new UrlResource("file:" + receipt.get().getSavedPath());
        }
        else {
            return null;
        }
    }
}
