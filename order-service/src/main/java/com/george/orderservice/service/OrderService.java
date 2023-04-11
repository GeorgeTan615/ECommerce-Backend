package com.george.orderservice.service;

import com.george.orderservice.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final KafkaTemplate<String,CartDto> cartDtoKafkaTemplate;
    private final KafkaTemplate<String,StripeForm> stripeFormKafkaTemplate;

    public String placeOrder(CartDto cartDto){
        try{
            cartDtoKafkaTemplate.send("ordersPlacedTopic",cartDto);
            log.info("Message sent to kafka topic");
            return "Order placed successfully!";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
    public void getAndSendStripeForm(ReservationDto reservationDto){
        log.info("Handling reservation {} which ends at {}",
                reservationDto.getId(),
                reservationDto.getExpirationDateTime()
        );
        StripeForm stripeForm = StripeForm.builder()
                .cardName("George Tan Juan Sheng")
                .cardNumber("123456789")
                .currency("USD")
                .paymentAmount(BigDecimal.valueOf(1000))
                .reservationDto(reservationDto)
                .build();
        stripeFormKafkaTemplate.send("paymentsReadyTopic",stripeForm);
    }
}
