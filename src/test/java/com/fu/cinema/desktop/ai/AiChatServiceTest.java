package com.fu.cinema.desktop.ai;

import com.fu.cinema.entity.Movie;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private MovieService movieService;

    @Mock
    private ShowtimeService showtimeService;

    private AiChatService aiChatService;

    @BeforeEach
    void setUp() {
        aiChatService = new AiChatService(movieService, showtimeService);
    }

    @Test
    @DisplayName("AiChatService: Khi API Key rỗng hoặc chưa cấu hình, trả về tin nhắn nhắc nhở")
    void testApiKeyMissing() throws Exception {
        ReflectionTestUtils.setField(aiChatService, "apiKey", "");

        ChatMessage response = aiChatService.sendMessage("Xin chào", List.of());
        assertNotNull(response);
        assertFalse(response.fromUser());
        assertTrue(response.text().contains("Chưa tìm thấy Gemini API Key"));
    }

    @Test
    @DisplayName("AiChatService: System prompt chứa đầy đủ Guardrails, RAG nội quy và danh sách phim")
    void testSystemPromptConstruction() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Mai");
        movie.setDuration(120);
        when(movieService.findSchedulable()).thenReturn(List.of(movie));

        ReflectionTestUtils.setField(aiChatService, "apiKey", "");

        String systemPrompt = (String) ReflectionTestUtils.invokeMethod(aiChatService, "buildSystemPrompt");
        assertNotNull(systemPrompt);

        // Kiểm tra Guardrails
        assertTrue(systemPrompt.contains("NGUYÊN TẮC GIỚI HẠN PHẠM VI"));
        assertTrue(systemPrompt.contains("CHỈ ĐƯỢC PHÉP"));

        // Kiểm tra RAG Context
        assertTrue(systemPrompt.contains("NỘI QUY VÀ CHÍNH SÁCH"));

        // Kiểm tra danh sách phim thực tế
        assertTrue(systemPrompt.contains("Mai"));
        assertTrue(systemPrompt.contains("120 phút"));

        // Kiểm tra hướng dẫn tạo thẻ đặt vé [BOOK:...]
        assertTrue(systemPrompt.contains("[BOOK:showtimeId|tenPhim|tenPhong|gioChieu|giaVe]"));
    }

    @Test
    @DisplayName("AiChatService: Trích xuất chính xác thẻ [BOOK:...] thành danh sách hành động đặt vé")
    void testBookTagParsing() {
        String botReplyWithTag = "Tôi tìm thấy suất chiếu lý tưởng cho bạn: [BOOK:45|Lật Mặt 7|Cinema 2|20:00 08/11|85000]. Bạn bấm vào để đặt nhé!";

        java.util.regex.Pattern pattern = (java.util.regex.Pattern) ReflectionTestUtils.getField(AiChatService.class, "BOOK_PATTERN");
        assertNotNull(pattern);

        java.util.regex.Matcher matcher = pattern.matcher(botReplyWithTag);
        assertTrue(matcher.find());
        assertEquals("45", matcher.group(1));
        assertEquals("Lật Mặt 7", matcher.group(2));
        assertEquals("Cinema 2", matcher.group(3));
        assertEquals("20:00 08/11", matcher.group(4));
        assertEquals("85000", matcher.group(5));

        String cleanText = matcher.replaceAll("").trim();
        assertFalse(cleanText.contains("[BOOK:"));
        assertTrue(cleanText.contains("Tôi tìm thấy suất chiếu"));
    }

    @Test
    @DisplayName("AiChatService: Gọi thử API Gemini với API key cấu hình (nếu có)")
    void testRealGeminiCall() throws Exception {
        String testKey = System.getProperty("gemini.api.key", System.getenv("GEMINI_API_KEY"));
        org.junit.jupiter.api.Assumptions.assumeTrue(testKey != null && !testKey.isBlank(), "Bỏ qua test nếu không có API key");
        ReflectionTestUtils.setField(aiChatService, "apiKey", testKey);
        ChatMessage response = aiChatService.sendMessage("Xin chào, bạn là ai?", List.of());
        assertNotNull(response);
        assertFalse(response.text().contains("404"), "Phản hồi không được chứa lỗi 404");
        assertTrue(response.text().toLowerCase().contains("fu cinema") || response.text().toLowerCase().contains("trợ lý"));
    }
}

