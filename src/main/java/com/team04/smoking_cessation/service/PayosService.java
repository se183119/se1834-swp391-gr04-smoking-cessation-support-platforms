package com.team04.smoking_cessation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayosService {

    @Value("${payos.client.id}")
    private String clientId;

    @Value("${payos.api.key}")
    private String apiKey;

    @Value("${payos.checksum.key}")
    private String checksumKey;

    @Value("${payos.api.url}")
    private String apiUrl;

    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * Tạo link thanh toán PayOS
     */
    public Map<String, Object> createPaymentLink(String orderCode, BigDecimal amount, String description, String returnUrl, String cancelUrl) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderCode", orderCode);
        requestBody.put("amount", amount.intValue());
        requestBody.put("description", description);
        requestBody.put("returnUrl", returnUrl);
        requestBody.put("cancelUrl", cancelUrl);
        requestBody.put("signature", generateSignature(orderCode, amount.intValue(), description));

        return webClientBuilder.build()
                .post()
                .uri(apiUrl + "/v2/payment-requests")
                .header("x-client-id", clientId)
                .header("x-api-key", apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     * Kiểm tra trạng thái giao dịch
     */
    public Map<String, Object> checkTransactionStatus(String transactionId) {
        return webClientBuilder.build()
                .get()
                .uri(apiUrl + "/v2/transactions/" + transactionId)
                .header("x-client-id", clientId)
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     * Xác thực webhook từ PayOS
     */
    public boolean verifyWebhookSignature(String data, String signature) {
        String expectedSignature = generateWebhookSignature(data);
        return expectedSignature.equals(signature);
    }

    /**
     * Tạo signature cho request
     */
    private String generateSignature(String orderCode, int amount, String description) {
        String data = orderCode + amount + description;
        return org.apache.commons.codec.digest.HmacUtils.hmacSha256Hex(checksumKey, data);
    }

    /**
     * Tạo signature cho webhook
     */
    private String generateWebhookSignature(String data) {
        return org.apache.commons.codec.digest.HmacUtils.hmacSha256Hex(checksumKey, data);
    }
} 