package com.fu.cinema.desktop.ai;

import com.fu.cinema.entity.Movie;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

    private static final List<String> MODEL_CANDIDATES = List.of(
            "gemini-flash-lite-latest",
            "gemini-3.8-flash",
            "gemini-flash-latest"
    );
    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=";
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm dd/MM");
    private static final Pattern BOOK_PATTERN = Pattern.compile("\\[BOOK:(\\d+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|\\]]+)\\]");

    private final MovieService movieService;
    private final ShowtimeService showtimeService;

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private String cachedPolicyText = null;

    /**
     * Gửi tin nhắn đến Google Gemini với Context: Guardrails + RAG Nội quy + Lịch chiếu thực tế + Lịch sử chat.
     */
    public ChatMessage sendMessage(String userMessage, List<ChatMessage> history) throws Exception {
        if (apiKey == null || apiKey.isBlank() || apiKey.contains("CHANGE_ME")) {
            return ChatMessage.bot("⚠️ Chưa tìm thấy Gemini API Key trong `application-local.properties` (khoá `gemini.api.key`). Quý khách vui lòng kiểm tra lại cấu hình nhé.", List.of());
        }

        String systemPrompt = buildSystemPrompt();

        // Xây dựng JSON payload hoàn toàn độc lập (Pure Java, không phụ thuộc thư viện ngoài)
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"system_instruction\":{\"parts\":[{\"text\":\"").append(escapeJson(systemPrompt)).append("\"}]},");
        json.append("\"contents\":[");

        boolean first = true;
        if (history != null) {
            for (ChatMessage msg : history) {
                if (!first) json.append(",");
                first = false;
                String role = msg.fromUser() ? "user" : "model";
                json.append("{\"role\":\"").append(role).append("\",\"parts\":[{\"text\":\"")
                        .append(escapeJson(msg.text())).append("\"}]}");
            }
        }
        if (!first) json.append(",");
        json.append("{\"role\":\"user\",\"parts\":[{\"text\":\"").append(escapeJson(userMessage)).append("\"}]}");
        json.append("],");
        json.append("\"generationConfig\":{\"temperature\":0.4,\"maxOutputTokens\":800}");
        json.append("}");

        HttpResponse<String> response = null;
        String lastError = null;

        for (String model : MODEL_CANDIDATES) {
            String url = String.format(GEMINI_API_BASE_URL, model) + apiKey.trim();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8))
                    .build();

            try {
                HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (res.statusCode() == 200) {
                    response = res;
                    log.info("Gemini call succeeded with model: {}", model);
                    break;
                } else {
                    log.warn("Gemini model {} returned status {}: {}", model, res.statusCode(), res.body());
                    lastError = "HTTP " + res.statusCode();
                }
            } catch (Exception ex) {
                log.warn("Gemini model {} request failed: {}", model, ex.getMessage());
                lastError = ex.getMessage();
            }
        }

        if (response == null || response.statusCode() != 200) {
            return ChatMessage.bot("⚠️ Kết nối tới dịch vụ AI Gemini gặp phản hồi lỗi (" + lastError + "). Quý khách vui lòng thử lại sau giây lát nhé.", List.of());
        }

        String rawReply = extractTextFromGeminiResponse(response.body());
        if (rawReply.isBlank()) {
            return ChatMessage.bot("Dạ xin lỗi quý khách, tôi chưa hiểu rõ yêu cầu. Quý khách vui lòng diễn đạt lại giúp tôi nhé!", List.of());
        }

        // Bóc tách các thẻ [BOOK:id:title:room:time:price] để tạo action buttons
        List<ShowtimeAction> actions = new ArrayList<>();
        Matcher matcher = BOOK_PATTERN.matcher(rawReply);
        while (matcher.find()) {
            Long stId = Long.parseLong(matcher.group(1));
            String title = matcher.group(2).trim();
            String room = matcher.group(3).trim();
            String time = matcher.group(4).trim();
            String price = matcher.group(5).trim();
            actions.add(new ShowtimeAction(stId, title, room, time, price));
        }

        String cleanText = matcher.replaceAll("").trim();
        return ChatMessage.bot(cleanText, actions);
    }

    /**
     * Trích xuất văn bản trả lời từ JSON của Gemini (Không phụ thuộc Jackson).
     */
    private String extractTextFromGeminiResponse(String json) {
        String key = "\"text\": \"";
        int idx = json.indexOf(key);
        if (idx == -1) {
            return "";
        }
        int start = idx + key.length();
        StringBuilder sb = new StringBuilder();
        boolean escape = false;

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break; // Hết chuỗi text
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : raw.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Tổng hợp System Prompt: Guardrails + RAG Nội quy + Danh sách Phim & Suất chiếu thực tế.
     */
    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();

        sb.append("BẠN LÀ TRỢ LÝ ẢO ĐỘC QUYỀN CỦA HỆ THỐNG RẠP PHIM FU CINEMA (FU Cinema Ticket System).\n");
        sb.append("Phong cách trả lời: Thân thiện, lịch sự, chuyên nghiệp, ngắn gọn, xưng hô 'tôi' và 'quý khách'.\n\n");

        sb.append("=== NGUYÊN TẮC GIỚI HẠN PHẠM VI (QUAN TRỌNG NHẤT - GUARDRAILS) ===\n");
        sb.append("1. Bạn CHỈ ĐƯỢC PHÉP trả lời các câu hỏi liên quan đến: phim đang chiếu, suất chiếu, giá vé, nội quy rạp và dịch vụ của FU Cinema.\n");
        sb.append("2. Nếu người dùng hỏi bất kỳ câu hỏi nào NGOÀI LỀ (ví dụ: toán học, lập trình, thời tiết, chính trị, dịch thuật, thơ ca, kiến thức chung không liên quan đến rạp phim), bạn BẮT BUỘC PHẢI TỪ CHỐI lịch sự:\n");
        sb.append("   'Dạ xin lỗi quý khách, tôi là trợ lý ảo của rạp FU Cinema nên chỉ có thể hỗ trợ các thông tin về phim, suất chiếu, giá vé và nội quy của rạp thôi ạ. Quý khách cần hỗ trợ đặt vé hoặc tìm phim gì không ạ?'\n\n");

        sb.append("=== TÀI LIỆU NỘI QUY VÀ CHÍNH SÁCH RẠP (RAG CONTEXT) ===\n");
        sb.append(loadPolicyText()).append("\n\n");

        sb.append("=== DỮ LIỆU PHIM VÀ SUẤT CHIẾU THỰC TẾ HIỆN CÓ TẠI RẠP ===\n");
        appendRealtimeMoviesAndShowtimes(sb);

        sb.append("\n=== HƯỚNG DẪN GỢI Ý ĐẶT VÉ (RẤT QUAN TRỌNG) ===\n");
        sb.append("Khi người dùng nói họ muốn xem phim gì đó và nêu thời gian rảnh (ví dụ: 'Tôi muốn xem phim Mai, rảnh từ 18h đến 21h'), bạn hãy kiểm tra danh sách suất chiếu ở trên và gợi ý các suất phù hợp nhất.\n");
        sb.append("Với MỖI suất chiếu bạn gợi ý, bạn BẮT BUỘC phải chèn một mã thẻ theo đúng cú pháp sau vào câu trả lời để hệ thống tạo nút Đặt vé:\n");
        sb.append("[BOOK:showtimeId|tenPhim|tenPhong|gioChieu|giaVe]\n");
        sb.append("Ví dụ: 'Tôi tìm thấy suất chiếu phù hợp cho bạn: [BOOK:12|Mai|Cinema 1|19:30 08/11|80000đ]'\n");

        return sb.toString();
    }

    private void appendRealtimeMoviesAndShowtimes(StringBuilder sb) {
        try {
            List<Movie> movies = movieService.findSchedulable();
            if (movies.isEmpty()) {
                sb.append("Hiện tại chưa có phim nào trong lịch chiếu.\n");
                return;
            }

            for (Movie m : movies) {
                sb.append(String.format("- Phim: %s (Thời lượng: %d phút, Phân loại: %s)\n",
                        m.getTitle(), m.getDuration(), m.getAgeRating() != null ? m.getAgeRating().name() : "P"));

                List<Showtime> showtimes = showtimeService.findUpcomingByMovie(m.getMovieId());
                if (showtimes.isEmpty()) {
                    sb.append("  (Chưa có suất chiếu sắp tới)\n");
                } else {
                    for (Showtime st : showtimes) {
                        sb.append(String.format("  * Suất chiếu ID: %d | Phòng: %s | Bắt đầu: %s | Giá: %s VNĐ\n",
                                st.getShowtimeId(),
                                st.getRoom().getRoomName(),
                                st.getStartTime().format(TIME_FMT),
                                st.getTicketPrice() != null ? st.getTicketPrice().toPlainString() : "80000"));
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Không thể tải danh sách phim và suất chiếu cho AI: {}", ex.getMessage());
            sb.append("Không thể lấy dữ liệu suất chiếu thời gian thực.\n");
        }
    }

    private String loadPolicyText() {
        if (cachedPolicyText != null) {
            return cachedPolicyText;
        }
        try (InputStream is = getClass().getResourceAsStream("/desktop/ai/cinema-policy.txt")) {
            if (is != null) {
                cachedPolicyText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                return cachedPolicyText;
            }
        } catch (Exception ex) {
            log.error("Không thể đọc file cinema-policy.txt", ex);
        }
        return "Nội quy rạp: Đúng độ tuổi quy định, không mang đồ ăn ngoài nặng mùi, tối đa 8 vé/đơn.";
    }
}
