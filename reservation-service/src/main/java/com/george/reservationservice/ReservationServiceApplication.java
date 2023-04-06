package com.george.reservationservice;

import com.george.reservationservice.dto.CartDto;
import com.george.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceApplication {
    private final ReservationService reservationService;

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class,args);
    }

    @KafkaListener(topics = "ordersPlacedTopic")
    public void validateOrder(CartDto cartDto) throws Exception {
        log.info("Orders for cart {} is validating...",cartDto.getId());
        reservationService.validateAndCreateReservation(cartDto);

    }
}
