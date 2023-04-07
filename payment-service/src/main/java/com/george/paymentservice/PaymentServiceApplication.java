package com.george.paymentservice;

import com.george.paymentservice.dto.StripeForm;
import com.george.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
public class PaymentServiceApplication {
    private final PaymentService paymentService;

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class,args);
    }

    @KafkaListener(topics = "paymentsReadyTopic")
    public void handlePayments(StripeForm stripeForm){
        paymentService.handlePayments(stripeForm);

    }
}
