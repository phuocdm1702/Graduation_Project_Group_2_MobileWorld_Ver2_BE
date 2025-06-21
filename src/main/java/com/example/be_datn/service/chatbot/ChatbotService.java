package com.example.be_datn.service.chatbot;

import com.example.be_datn.dto.chatbot.request.ChatRequest;
import com.example.be_datn.dto.chatbot.response.ChatResponse;
import com.example.be_datn.entity.product.SanPham;
import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import com.example.be_datn.repository.product.SanPhamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatbotService {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private OkHttpClient client;

    @Value("${openai.api.key}")
    private String API_KEY;

    private final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatResponse getChatbotResponse(ChatRequest request) throws Exception {
        // Tìm kiếm sản phẩm từ DB dựa trên tin nhắn
        List<SanPham> sanPhams = sanPhamRepository.findByDeletedFalse(null)
                .stream()
                .filter(sanPham -> {
                    String searchTerm = request.getMessage().toLowerCase();
                    return (sanPham.getTenSanPham() != null && sanPham.getTenSanPham().toLowerCase().contains(searchTerm)) ||
                            (sanPham.getMa() != null && sanPham.getMa().toLowerCase().contains(searchTerm)) ||
                            (sanPham.getTenSanPham() != null && sanPham.getTenSanPham().toLowerCase().contains("iphone") && searchTerm.contains("iphone")) ||
                            (sanPham.getTenSanPham() != null && sanPham.getTenSanPham().toLowerCase().contains("samsung") && searchTerm.contains("samsung"));
                })
                .toList();

        String productInfo;
        if (sanPhams.isEmpty()) {
            productInfo = "Không tìm thấy sản phẩm phù hợp.";
        } else {
            List<String> productInfoList = sanPhams.stream().flatMap(sanPham -> {
                List<Object[]> chiTietList = chiTietSanPhamRepository.findGroupedProductsBySanPhamId(sanPham.getId());
                return chiTietList.stream().map(ct -> String.format(
                        "Tên: %s, Mã: %s, Màu: %s, RAM: %s, ROM: %s, Giá: %s VND, Số lượng: %d",
                        ct[1], ct[0], ct[2], ct[3], ct[4], ct[6], ((Number) ct[5]).intValue()
                ));
            }).limit(10).collect(Collectors.toList());
            productInfo = String.join("\n", productInfoList);
        }

        // Tạo prompt cho OpenAI
        String prompt = String.format("""
                Bạn là một nhân viên bán hàng chuyên nghiệp tại cửa hàng điện thoại thông minh MobileWorld.
                Dựa trên thông tin sản phẩm sau, hãy trả lời câu hỏi của khách hàng một cách ngắn gọn, thân thiện và chính xác:
                %s
                Nếu không tìm thấy sản phẩm, hãy gợi ý khách hàng liên hệ qua hotline 1800 000 hoặc đến cửa hàng.
                Câu hỏi khách hàng: %s
                """, productInfo, request.getMessage());

        // Tạo payload JSON sử dụng ObjectMapper
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "gpt-3.5-turbo");

        ObjectNode systemMessage = objectMapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", prompt);

        ObjectNode userMessage = objectMapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", request.getMessage());

        requestBody.set("messages", objectMapper.createArrayNode().add(systemMessage).add(userMessage));

        String jsonPayload = objectMapper.writeValueAsString(requestBody);
        System.out.println("Payload: " + jsonPayload);

        // Gọi OpenAI API
        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
        Request apiRequest = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(apiRequest).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body().string();
                System.out.println("Error Response: " + errorBody);
                throw new Exception("Error calling OpenAI API: " + response + ", Details: " + errorBody);
            }

            String responseBody = response.body().string();
            var jsonNode = objectMapper.readTree(responseBody);
            String reply = jsonNode.path("choices").get(0).path("message").path("content").asText();

            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setReply(reply);
            return chatResponse;
        }
    }
}
