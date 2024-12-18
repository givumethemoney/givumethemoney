package com.hey.givumethemoney.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.givumethemoney.domain.OCRResult;
import com.hey.givumethemoney.domain.Receipt;
import com.hey.givumethemoney.repository.NaverOCRRepository;
import com.hey.givumethemoney.repository.ReceiptRepository;

@Service
public class NaverOCRService {

    private final NaverOCRRepository naverOCRRepository;
    private final ReceiptRepository receiptRepository;

    // 생성자 추가
    @Autowired
    public NaverOCRService(NaverOCRRepository naverOCRRepository, ReceiptRepository receiptRepository) {
        this.naverOCRRepository = naverOCRRepository;
        this.receiptRepository = receiptRepository;
    }

    // general 무료로 되면 바꿔서 해보기
    private static final String OCR_API_URL = "https://700qq10mtd.apigw.ntruss.com/custom/v1/36052/9e79aba8594e508b85cc982d1491a1c9216c3282ca0f4097fedf75bac7ec634d/infer";
    
    @Value("${naver.ocr.secret-key}")
    private String secretKey;

    public List<OCRResult> sendImageToOCR(List<Receipt> receipts) {
        List<OCRResult> ocrResults = new ArrayList<>();

        try {
            // 이미지 객체들의 savedPath를 추출하여 imageUrls 리스트에 저장
            List<String> imageUrls = receipts.stream()
                    .map(Receipt::getImageUrl)  // 각 이미지 객체의 imageUrl를 가져옴
                    .collect(Collectors.toList());
    
            // Create HttpClient instance
            HttpClient httpClient = HttpClient.newHttpClient();
    
            // Generate the current timestamp
            long timestamp = Instant.now().getEpochSecond();
    
            // 각 영수증에 대해 OCR 요청을 보냄
            for (String imageUrl : imageUrls) {
                // Prepare the JSON body with the image URL and other parameters
                String jsonBody = String.format("{\n" +
                                "  \"images\": [\n" +
                                "    {\n" +
                                "      \"format\": \"png\",\n" +
                                "      \"name\": \"img ocr test\",\n" +
                                "      \"url\": \"%s\"\n" +
                                "    }\n" +
                                "  ],\n" +
                                "  \"lang\": \"ko\",\n" +
                                "  \"version\": \"V2\",\n" +
                                "  \"requestId\": \"string\",\n" +
                                "  \"timestamp\": %d,\n" +
                                "  \"resultType\": \"string\"\n" +
                                "}", imageUrl, timestamp);
        
                // Create HttpRequest
                HttpRequest postRequest = HttpRequest.newBuilder()
                        .uri(URI.create(OCR_API_URL))
                        .header("Content-Type", "application/json")
                        .header("X-OCR-SECRET", secretKey)
                        .POST(BodyPublishers.ofString(jsonBody))
                        .build();
        
                // Send the request and receive the response
                HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        
                System.out.println("ocr Response: " + response);
                if (response.statusCode() == 200) {
                    // Parse JSON response
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonResponse = objectMapper.readTree(response.body());
        
                    // Extract OCR fields (adjust the path as necessary based on the actual response format)
                    JsonNode fields = jsonResponse.at("/images/0/fields");
        
                    // Variables to store the extracted information
                    String productName = null;
                    String quantity = null;
                    String unitPrice = null;
                    String totalAmount = null;
        
                    // Iterate through fields to find the desired information
                    for (JsonNode field : fields) {
                        String name = field.at("/name").asText();
                        String value = field.at("/inferText").asText();
        
                        // Check for product name 
                        if ("상품명".equals(name)) {
                            productName = value;
                        }
        
                        // Check for quantity (수량)
                        if ("수량".equals(name)) {
                            quantity = value;
                        }
        
                        // Check for unit price (단가)
                        if ("단가".equals(name)) {
                            unitPrice = value;
                        }
        
                        // Check for total amount (총금액)
                        if ("총금액".equals(name)) {
                            totalAmount = value;
                        }
                    }
                    // If quantity or unit price is missing, set defaults
                    if (quantity == null) {
                        quantity = "1";  // Default to 1 if quantity is missing
                    }

                    if (unitPrice == null) {
                        unitPrice = totalAmount;  // Default unit price to totalAmount if missing
                    }

                    // 디버깅 출력
                    System.out.println("Extracted values - Product: " + productName + ", Quantity: " + quantity + ", Unit Price: " + unitPrice + ", Total Amount: " + totalAmount);

                    // 추출된 정보를 기반으로 객체 생성하여 리스트에 추가
                    if (productName != null && quantity != null && unitPrice != null && totalAmount != null) {
                        ocrResults.add(new OCRResult(productName, quantity, unitPrice, totalAmount));
                    }
                    } else {
                        // return "OCR API call failed with status: " + response.statusCode();
                    }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error while sending image to OCR API", e);
        }
        System.out.println("ocrResults: " + ocrResults);
        return ocrResults;
    }
    
}
