package com.george.orderservice;

import com.george.orderservice.dto.ReservationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@KafkaListener(topics = "ordersReservedTopic")
	public void handleOrdersReserved(ReservationDto reservationDto){
		log.info("Handling reservation {} which ends at {}",
				reservationDto.getId(),
				reservationDto.getExpirationDateTime()
		);
	}

}
