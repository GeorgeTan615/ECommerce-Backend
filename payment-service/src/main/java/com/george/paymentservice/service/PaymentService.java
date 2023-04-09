package com.george.paymentservice.service;

import com.george.paymentservice.dto.StripeForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final KafkaTemplate<String,StripeForm> kafkaTemplate;

    public void handlePayments(StripeForm stripeForm){
        log.info("Handling payment for {}'s card for reservation {}",
                stripeForm.getCardName(),
                stripeForm.getReservationDto().getId());
        if (stripeForm.getReservationDto()
                .getExpirationDateTime()
                .isBefore(LocalDateTime.now())){
            return;
        }

        // Assuming there is Stripe Processing logic
        kafkaTemplate.send("paymentsCompletedTopic",stripeForm);
    }
}
