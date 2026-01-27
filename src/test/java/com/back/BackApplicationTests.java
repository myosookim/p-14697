package com.back;

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

    @Test
    @DisplayName("FAQ 게시글 초기화 테스트")
    void t1() {
        long count = faqRepository.count();
        System.out.println("FAQ 게시글 수: " + count);
        assert (count >= 1);
    }

    @Test
    @DisplayName("PGroonga FTS 검색 테스트")
    void t2() {
        List<Faq> results = faqService.searchFaq("배송");
        assertFalse(results.isEmpty());
        assertTrue(results.stream()
                .anyMatch(faq -> faq.getQuestion().contains("배송") || faq.getAnswer().contains("배송")));
        System.out.println("FTS 검색 결과:");
        results.forEach(System.out::println);
    }

    @Test
    @DisplayName("Ai Controller 테스트")
    void t3(){
        webTestClient.get()
                .uri(uri->
                        uri.path("/api/v1/ai/chat")
                                .queryParam("message","배송은 얼마나 걸리나요?")
                                .build())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .collectList()
                .map(list->String.join("",list))
                .doOnNext(response->{
                    assertNotNull(response);
                    assertFalse(response.isEmpty());
                    System.out.println("AI 응답: " + response);
                })
                .block();

    }

}
