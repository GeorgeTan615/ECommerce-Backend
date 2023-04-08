package com.george.notificationservice;

import com.george.notificationservice.dto.StripeForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;
import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
@EnableDiscoveryClient
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class,args);
    }

    @KafkaListener(topics="paymentsCompletedTopic")
    public void handleNotification(StripeForm stripeForm){
        // Assuming we have logic to send emails out
        String userId = stripeForm.getReservationDto().getUserId();

        // Get the orderLineItems paid for with its quantity
        String orderLineItemsInfo = stripeForm.getReservationDto()
                                                    .getCartDto()
                                                    .getOrderLineItemDtoList()
                                                    .stream()
                                                    .map(orderLineItemDto ->
                                                            orderLineItemDto.getProductId() + ":"+orderLineItemDto.getQuantity())
                                                    .collect(Collectors.joining("\n"));
        log.info("Payment completed for - {}\n{}", userId, orderLineItemsInfo);
    }

}
