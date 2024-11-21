package com.hey.givumethemoney.service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.givumethemoney.repository.NaverOCRRepository;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;

@Service
public class NaverOCRService {
    private final NaverOCRRepository naverOCRRepository;
     // 생성자 추가
    public NaverOCRService(NaverOCRRepository naverOCRRepository) {
        this.naverOCRRepository = naverOCRRepository;
    }

    private static final String OCR_API_URL = "https://700qq10mtd.apigw.ntruss.com/custom/v1/36052/9e79aba8594e508b85cc982d1491a1c9216c3282ca0f4097fedf75bac7ec634d/infer";
    @Value("${naver.ocr.secret-key}")
    private String secretKey;

    public String sendImageToOCR(File imageFile) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(OCR_API_URL);

            // Add Headers
            postRequest.addHeader("Content-Type", "multipart/form-data");
            postRequest.addHeader("X-OCR-SECRET", secretKey);

            // Build multipart entity
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("image", imageFile)
                    .build();

            postRequest.setEntity(entity);

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    // Parse JSON response
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonResponse = objectMapper.readTree(response.getEntity().getContent());

                    // Extract information (customize as per your JSON structure)
                    JsonNode fields = jsonResponse.at("/images/0/fields");
                    StringBuilder extractedText = new StringBuilder();
                    fields.forEach(field -> {
                        String name = field.at("/name").asText();
                        String value = field.at("/value").asText();
                        extractedText.append(name).append(": ").append(value).append("\n");
                    });

                    return extractedText.toString();
                } else {
                    return "OCR API request failed. Status Code: " + statusCode;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while sending image to OCR API", e);
        }
    }

    // public static void main(String[] args) {
    //     NaverOCRService ocrService = new NaverOCRService();
    //     File imageFile = new File("path/to/your/image.jpg"); // Replace with the actual image file path
    //     String result = ocrService.sendImageToOCR(imageFile);
    //     System.out.println(result);
    // }
}
