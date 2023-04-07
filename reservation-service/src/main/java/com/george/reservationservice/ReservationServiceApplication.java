package com.george.reservationservice;

import com.george.reservationservice.dto.CartDto;
import com.george.reservationservice.dto.StripeForm;
import com.george.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ReservationServiceApplication {
    private final ReservationService reservationService;

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class,args);
    }

    @KafkaListener(topics = "ordersPlacedTopic")
    public void validateOrder(CartDto cartDto){
        try{
            log.info("Orders for cart {} is validating...",cartDto.getId());
            reservationService.validateAndCreateReservation(cartDto);
        }
        catch(RuntimeException e){
            log.info(e.getMessage());
            log.info("Validate order failed");
        }

    }

    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional
    public void removeStaleReservations(){
        reservationService.removeStaleReservations();
    }

    @KafkaListener(topics = "paymentsCompletedTopic")
    public void removePaidReservations(StripeForm stripeForm){
        reservationService.removePaidReservations(stripeForm);

    }
}
