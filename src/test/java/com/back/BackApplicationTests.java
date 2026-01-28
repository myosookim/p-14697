package com.back;

import com.back.ai.service.ChatService;
import com.back.faq.entity.Faq;
import com.back.faq.repository.FaqRepository;
import com.back.faq.service.FaqService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class BackApplicationTests {
    @Autowired
    private FaqRepository faqRepository;
    @Autowired
    private FaqService faqService;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ChatService chatService;

    @Test
    @DisplayName("FAQ 게시글 초기화 테스트")
    void t1() {
        long count = faqRepository.count();
        System.out.println("FAQ 게시글 수: " + count);
        assert (count >= 1);
    }

    @Test
    @DisplayName("FAQ 기반 AI 챗봇 테스트")
    void t2(){
        // given
        String userMessage = "배송은 얼마나 걸려요?";

        // when & then
        String response = chatService.streamChatResponse(userMessage)
                .collectList()
                .map(list -> String.join("", list))
                .block();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        System.out.println("AI 응답: " + response);
    }

    @Test
    @DisplayName("SSE 스트리밍 API 테스트")
    void t3(){
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/ai/chat")
                        .queryParam("message", "환불 방법을 알려주세요")
                        .build())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .collectList()
                .map(list -> String.join("", list))
                .doOnNext(response -> {
                    assertNotNull(response);
                    assertFalse(response.isEmpty());
                    System.out.println("SSE 응답: " + response);
                })
                .block();
    }

}
