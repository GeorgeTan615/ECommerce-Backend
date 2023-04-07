package com.george.orderservice;

import com.george.orderservice.dto.ReservationDto;
import com.george.orderservice.dto.StripeForm;
import com.george.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

import java.math.BigDecimal;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
@Slf4j
public class OrderServiceApplication {
	private final OrderService orderService;

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@KafkaListener(topics = "ordersReservedTopic")
	public void handleOrdersReserved(ReservationDto reservationDto){
		log.info("Order {} has been successfully reserved, proceeding to payment",reservationDto.getId());
		orderService.getAndSendStripeForm(reservationDto);
	}

	@KafkaListener(topics = "paymentsCompletedTopic")
	public void handleOrdersPaid(StripeForm stripeForm){
		log.info("Order {} has been paid successfully, order process ends",stripeForm.getReservationDto().getId());
	}

}
