package com.hey.givumethemoney.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class ChatbotService {

    private final String dialogflowProjectId = "givumethemoney";
    private final String sessionId = UUID.randomUUID().toString(); // 세션 ID를 고유하게 설정
    private final String dialogflowApiUrl = "https://dialogflow.googleapis.com/v2/projects/{your-project-id}/agent/sessions/{session-id}:detectIntent";
    private final DonationService donationService;

    // JSON 파일 상대 경로
    private final String jsonFilePath = "config/givumethemoney-40f84b2585de.json";

    @Autowired
    public ChatbotService(DonationService donationService) {
        this.donationService = donationService;
    }


    public String getAnswerFromChatbot(String question) {
        try {

            // 서비스 계정 키 파일을 사용하여 OAuth 2.0 인증
            // InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/givumethemoney-40f84b2585de.json");
            // if (inputStream == null) {
            //     throw new FileNotFoundException("Resource not found: json/givumethemoney-40f84b2585de.json");
            // }
            // JSON 파일 경로를 동적으로 설정
            InputStream inputStream = new FileInputStream("/home/ubuntu/givumethemoney/config/givumethemoney-40f84b2585de.json");

            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

            // 액세스 토큰 얻기
            String accessToken = credentials.refreshAccessToken().getTokenValue();

            // DialogFlow API 호출하여 답변을 받는 로직
            String apiUrl = dialogflowApiUrl.replace("{your-project-id}", dialogflowProjectId)
                    .replace("{session-id}", sessionId);  // URL에 프로젝트 ID와 session-id 대체

            return callDialogflowApi(apiUrl, question, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
            return "챗봇에 오류가 발생했습니다. 다시 시도해주세요.";
        }
    }

    private String callDialogflowApi(String apiUrl, String question, String accessToken) {
        try {
            // HTTP 요청 설정
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken); // OAuth2 토큰 사용
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 요청 본문 (JSON 형식)
            String jsonInputString = "{"
                    + "\"queryInput\": {"
                    + "\"text\": {"
                    + "\"text\": \"" + question + "\","
                    + "\"languageCode\": \"ko-KR\""
                    + "}"
                    + "}"
                    + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 받기
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // API 응답에서 답변 추출
                return parseResponse(response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "챗봇에 오류가 발생했습니다. 다시 시도해주세요.";
        }
    }

    // API 응답에서 답변을 추출하는 메서드 (간단한 JSON 파싱)
    private String parseResponse(String response) {
        try {
            // JSON 파서 설정
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            
            // queryResult -> fulfillmentText 추출
            JsonNode queryResultNode = rootNode.path("queryResult");
            String intent = queryResultNode.path("intent").path("displayName").asText();  // intent 확인
            String fulfillmentText = queryResultNode.path("fulfillmentText").asText();
            
            if ("find-donations".equals(intent)) {
                JsonNode parametersNode = queryResultNode.path("parameters");
                JsonNode donationThemeNode = parametersNode.path("donationtheme");
            
                // donationThemeNode가 배열이면 첫 번째 값을 추출
                String keyword = "";
                if (donationThemeNode.isArray() && donationThemeNode.size() > 0) {
                    keyword = donationThemeNode.get(0).asText(); // 배열의 첫 번째 값 추출
                }
                System.out.println("\n\n\nkeyword: " + keyword);

                List<String> donationLinks = donationService.findDonationsByKeyword(keyword);
                System.out.println("donationLinks: " + donationLinks);

                if (donationLinks.isEmpty() || "관련된 기부가 없습니다.".equals(donationLinks.get(0))) {
                    return "관련된 기부가 없습니다.";
                } else {
                    StringBuilder responseText = new StringBuilder();
                    responseText.append(keyword + "와 관련된 기부는 다음에서 확인할 수 있습니다: <br>");
                    for (String link : donationLinks) {
                        responseText.append("<a href='" + link + "'>" + link + "</a><br>");
                    }
                    return responseText.toString();
                }
            }
            // fulfillmentText 값 반환
            if (fulfillmentText != null && !fulfillmentText.isEmpty()) {
                return fulfillmentText;
            } else {
                return "응답 내용이 없습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "응답 파싱에 실패했습니다.";
        }
    }
}
