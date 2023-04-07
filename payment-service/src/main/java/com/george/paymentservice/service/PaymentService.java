package com.george.paymentservice.service;

import com.george.paymentservice.dto.StripeForm;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.rmi.server.RemoteServer;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final KafkaTemplate<String,StripeForm> kafkaTemplate;

    public void handlePayments(StripeForm stripeForm){
        if (stripeForm.getReservationDto()
                .getExpirationDateTime()
                .isBefore(LocalDateTime.now())){
            return;
        }

        // Assuming there is Stripe Processing logic
        kafkaTemplate.send("paymentsCompletedTopic",stripeForm);
    }
}
