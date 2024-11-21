package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.repository.ImageRepository;
import com.hey.givumethemoney.repository.NaverOCRRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class NaverOCRService {

    private final NaverOCRRepository naverOCRRepository;
    private final ImageRepository imageRepository;

    // 생성자 추가
    @Autowired
    public NaverOCRService(NaverOCRRepository naverOCRRepository, ImageRepository imageRepository) {
        this.naverOCRRepository = naverOCRRepository;
        this.imageRepository = imageRepository;
    }

    private static final String OCR_API_URL = "https://700qq10mtd.apigw.ntruss.com/custom/v1/36052/9e79aba8594e508b85cc982d1491a1c9216c3282ca0f4097fedf75bac7ec634d/infer";
    
    @Value("${naver.ocr.secret-key}")
    private String secretKey;

    public String sendImageToOCR(Long donationId) {
        try {
            // 이미지 경로들을 가져오기
            List<Image> images = imageRepository.findImagesByDonationId(donationId);
            // 이미지 객체들의 savedPath를 추출하여 imageUrls 리스트에 저장
            List<String> imageUrls = images.stream()
            .map(Image::getSavedPath)  // 각 이미지 객체의 savedPath를 가져옴
            .collect(Collectors.toList());

            // 이미지 경로 중 하나라도 null이라면 오류 처리
            if (imageUrls.contains(null)) {
                return "One or more images not found.";
            }

            // Create HttpClient instance
            HttpClient httpClient = HttpClient.newHttpClient();

            // Generate the current timestamp
            long timestamp = Instant.now().getEpochSecond();

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
                            "}", imageUrls.get(0), timestamp);

            // Create HttpRequest
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(OCR_API_URL))
                    .header("Content-Type", "application/json")
                    .header("X-OCR-SECRET", secretKey)
                    .POST(BodyPublishers.ofString(jsonBody))
                    .build();

            // Send the request and receive the response
            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.body());

                // Extract OCR fields (adjust the path as necessary based on the actual response format)
                JsonNode fields = jsonResponse.at("/images/0/fields");
                StringBuilder extractedText = new StringBuilder();
                fields.forEach(field -> {
                    String name = field.at("/name").asText();
                    String value = field.at("/value").asText();
                    extractedText.append(name).append(": ").append(value).append("\n");
                });

                return extractedText.toString();
            } else {
                return "OCR API request failed. Status Code: " + response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error while sending image to OCR API", e);
        }
    }
}
