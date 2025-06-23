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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatbotService {
//    @Autowired
//    private SanPhamRepository sanPhamRepository;
//
//    @Autowired
//    private ChiTietSanPhamRepository chiTietSanPhamRepository;
//
//    @Autowired
//    private OkHttpClient client;
//
//    @Value("${openai.api.key}")
//    private String API_KEY;
//
//    private final String API_URL = "https://api.openai.com/v1/chat/completions";
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public ChatResponse getChatbotResponse(ChatRequest request) throws Exception {
//        // Tìm kiếm sản phẩm từ DB dựa trên tin nhắn
//        List<SanPham> sanPhams = sanPhamRepository.findByDeletedFalse(null)
//                .stream()
//                .filter(sanPham -> {
//                    String searchTerm = request.getMessage().toLowerCase();
//                    return (sanPham.getTenSanPham() != null && sanPham.getTenSanPham().toLowerCase().contains(searchTerm)) ||
//                            (sanPham.getMa() != null && sanPham.getMa().toLowerCase().contains(searchTerm)) ||
//                            (sanPham.getTenSanPham() != null && sanPham.getTenSanPham().toLowerCase().contains("iphone") && searchTerm.contains("iphone"))
//                            ||
//                            (sanPham.getTenSanPham() != null && sanPham.getTenSanPham().toLowerCase().contains("samsung") && searchTerm.contains("samsung"))
//                            ||
//                            (sanPham.getTenSanPham() != null && sanPham.getTenSanPham().toLowerCase().contains("xiaomi") && searchTerm.contains("xiaomi"))
//                            ||
//                            (sanPham.getTenSanPham() != null && sanPham.getTenSanPham().toLowerCase().contains("xiaomi") && searchTerm.contains("xiaomi"))
//
//                            ;
//                })
//                .toList();
//
//        String productInfo;
//        if (sanPhams.isEmpty()) {
//            productInfo = "Không tìm thấy sản phẩm phù hợp.";
//        } else {
//            List<String> productInfoList = sanPhams.stream().flatMap(sanPham -> {
//                List<Object[]> chiTietList = chiTietSanPhamRepository.findGroupedProductsBySanPhamId(sanPham.getId());
//                return chiTietList.stream().map(ct -> String.format(
//                        "Tên: %s, Mã: %s, Màu: %s, RAM: %s, ROM: %s, Giá: %s VND, Số lượng: %d",
//                        ct[1], ct[0], ct[2], ct[3], ct[4], ct[6], ((Number) ct[5]).intValue()
//                ));
//            }).limit(10).collect(Collectors.toList());
//            productInfo = String.join("\n", productInfoList);
//        }
//
//        // Tạo prompt cho OpenAI
//        String prompt = String.format("""
//                Bạn là một nhân viên bán hàng chuyên nghiệp tại cửa hàng điện thoại thông minh MobileWorld.
//                Dựa trên thông tin sản phẩm sau, hãy trả lời câu hỏi của khách hàng một cách ngắn gọn, thân thiện và chính xác:
//                %s
//                Nếu không tìm thấy sản phẩm, hãy gợi ý khách hàng liên hệ qua hotline 1800 000 hoặc đến cửa hàng.
//                Câu hỏi khách hàng: %s
//                """, productInfo, request.getMessage());
//
//        // Tạo payload JSON sử dụng ObjectMapper
//        ObjectNode requestBody = objectMapper.createObjectNode();
//        requestBody.put("model", "gpt-3.5-turbo");
//
//        ObjectNode systemMessage = objectMapper.createObjectNode();
//        systemMessage.put("role", "system");
//        systemMessage.put("content", prompt);
//
//        ObjectNode userMessage = objectMapper.createObjectNode();
//        userMessage.put("role", "user");
//        userMessage.put("content", request.getMessage());
//
//        requestBody.set("messages", objectMapper.createArrayNode().add(systemMessage).add(userMessage));
//
//        String jsonPayload = objectMapper.writeValueAsString(requestBody);
//        System.out.println("Payload: " + jsonPayload);
//
//        // Gọi OpenAI API
//        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
//        Request apiRequest = new Request.Builder()
//                .url(API_URL)
//                .addHeader("Authorization", "Bearer " + API_KEY)
//                .addHeader("Content-Type", "application/json")
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(apiRequest).execute()) {
//            if (!response.isSuccessful()) {
//                String errorBody = response.body().string();
//                System.out.println("Error Response: " + errorBody);
//                throw new Exception("Error calling OpenAI API: " + response + ", Details: " + errorBody);
//            }
//
//            String responseBody = response.body().string();
//            var jsonNode = objectMapper.readTree(responseBody);
//            String reply = jsonNode.path("choices").get(0).path("message").path("content").asText();
//
//            ChatResponse chatResponse = new ChatResponse();
//            chatResponse.setReply(reply);
//            return chatResponse;
//        }
//    }

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

    private static final Set<String> BRANDS = new HashSet<>(Arrays.asList("iphone", "samsung", "xiaomi", "oppo", "vivo", "realme", "huawei", "nokia", "sony"));

    public ChatResponse getChatbotResponse(ChatRequest request) throws Exception {
        String message = request.getMessage().toLowerCase();
        String brand = extractBrand(message);

        // Lấy tất cả sản phẩm, ưu tiên lọc theo thương hiệu nếu có
        List<SanPham> sanPhams = sanPhamRepository.findAllByDeletedFalse().stream()
                .filter(sanPham -> {
                    String tenSanPham = sanPham.getTenSanPham() != null ? sanPham.getTenSanPham().toLowerCase() : "";
                    return brand == null || tenSanPham.contains(brand) ||
                            (sanPham.getMa() != null && sanPham.getMa().toLowerCase().contains(message));
                })
                .toList();

        String productInfo;
        if (sanPhams.isEmpty()) {
            productInfo = "Không tìm thấy sản phẩm phù hợp.";
        } else {
            List<String> productInfoList = sanPhams.stream()
                    .flatMap(sanPham -> {
                        List<Object[]> chiTietList = chiTietSanPhamRepository.findGroupedProductsBySanPhamId(sanPham.getId());
                        return chiTietList.stream()
                                .map(ct -> String.format(
                                        "Tên: %s, Mã: %s, Màu: %s, RAM: %s, ROM: %s, Giá: %s VND, Số lượng: %d, " +
                                                "Công nghệ màn hình: %s, Dung lượng pin: %s, Hệ điều hành: %s, Camera: %s",
                                        ct[1], ct[0], ct[2], ct[3], ct[4], ct[6], ((Number) ct[5]).intValue(),
                                        getCongNgheManHinh(sanPham), getDungLuongPin(sanPham), getHeDieuHanh(sanPham), getCamera(sanPham)
                                ));
                    })
                    .collect(Collectors.toList());
            productInfo = String.join("\n", productInfoList);
        }

        // Tạo prompt linh hoạt cho OpenAI, để AI tự phân tích và trả lời
        String prompt = String.format("""
                Bạn là một nhân viên bán hàng chuyên nghiệp tại cửa hàng điện thoại thông minh MobileWorld.
                Dựa trên thông tin sản phẩm sau, hãy trả lời câu hỏi của khách hàng một cách ngắn gọn, thân thiện và chính xác, ngay cả khi câu hỏi mơ hồ hoặc không chứa thông số cụ thể:
                %s
                Nếu không tìm thấy sản phẩm phù hợp, hãy gợi ý khách hàng liên hệ qua hotline 1800 000 hoặc đến cửa hàng.
                Câu hỏi khách hàng: %s
                Hướng dẫn: Nếu câu hỏi là so sánh (ví dụ: 'iPhone nào tốt hơn?'), hãy so sánh dựa trên giá, thông số (RAM, ROM, camera, pin) và đưa ra gợi ý. Nếu câu hỏi chung (ví dụ: 'Điện thoại nào rẻ?'), hãy chọn sản phẩm có giá thấp nhất trong danh sách.
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

    private String extractBrand(String message) {
        return BRANDS.stream()
                .filter(message::contains)
                .findFirst()
                .orElse(null);
    }

    private String getCongNgheManHinh(SanPham sanPham) {
        return sanPham.getCongNgheManHinh() != null ? "OLED" : "Unknown"; // Thay bằng logic thực tế
    }

    private String getDungLuongPin(SanPham sanPham) {
        return sanPham.getIdPin() != null ? "5000mAh" : "Unknown"; // Thay bằng logic thực tế
    }

    private String getHeDieuHanh(SanPham sanPham) {
        return sanPham.getIdHeDieuHanh() != null ? "Android 13" : "Unknown"; // Thay bằng logic thực tế
    }

    private String getCamera(SanPham sanPham) {
        return sanPham.getIdCumCamera() != null ? "48MP + 12MP" : "Unknown"; // Thay bằng logic thực tế
    }
}
