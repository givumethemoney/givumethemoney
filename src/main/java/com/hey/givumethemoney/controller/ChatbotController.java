package com.hey.givumethemoney.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hey.givumethemoney.service.ChatbotService;

@Controller
public class ChatbotController {

    @Autowired
    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @GetMapping("/chatbot")
    public String chatbot() {
        return "chatbot";
    }

    @PostMapping("/chatbot/ask")
    @ResponseBody
    public Map<String, String> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = chatbotService.getAnswerFromChatbot(question);
        Map<String, String> response = new HashMap<>();
        response.put("answer", answer);
        return response;
    }

}
