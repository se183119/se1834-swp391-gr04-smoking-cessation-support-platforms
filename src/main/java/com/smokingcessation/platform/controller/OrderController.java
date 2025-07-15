package com.smokingcessation.platform.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smokingcessation.platform.dto.CreateOrderRequestDTO;
import com.smokingcessation.platform.service.OrderService;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.*;

@RestController
@RequestMapping("/order")
@CrossOrigin("*")
public class OrderController  {

    @Autowired
    private PayOS payOS;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/create-payment-link")
    public ResponseEntity<CheckoutResponseData> createPaymentLink() throws Exception {
        String domain = "http://localhost:3000/";
        Long orderCode = System.currentTimeMillis() / 1000;

        ItemData itemData = ItemData.builder()
                .name("Mỳ tôm Hảo Hảo ly")
                .quantity(1)
                .price(2000)
                .build();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(2000)
                .description("Thanh toán đơn hàng")
                .returnUrl(domain)
                .cancelUrl(domain)
                .item(itemData)
                .build();

        CheckoutResponseData result = payOS.createPaymentLink(paymentData);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create-order")
    public ResponseEntity<CheckoutResponseData> createOrder(
            @RequestBody CreateOrderRequestDTO dto
    ) throws Exception {
        CheckoutResponseData checkoutResponseData = orderService.createOrder(dto);
        return ResponseEntity.ok(checkoutResponseData);
    }

    @PostMapping("/webhook-payos")
    public ResponseEntity<String> handleWebhook(@RequestBody String rawJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Webhook webhook = objectMapper.readValue(rawJson, Webhook.class);
            WebhookData data = payOS.verifyPaymentWebhookData(webhook);
            orderService.handlePayOSWebhook(data);
            return ResponseEntity.ok("OK");
        } catch (Exception ex) {
            System.out.println("Webhook parse failed: " + ex.getMessage());
            return ResponseEntity.ok("IGNORE");
        }
    }



}
