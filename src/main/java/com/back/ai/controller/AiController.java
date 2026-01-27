package com.back.ai.controller;


import com.back.ai.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/chat")
    public Flux<String> chat(
            @RequestParam("message") String message
    ){
        return chatService.streamChatResponse(message);
    }
}