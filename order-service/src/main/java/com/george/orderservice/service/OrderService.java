package com.george.orderservice.service;

import com.george.orderservice.dto.*;
import com.george.orderservice.exception.CartNotFoundException;
import com.george.orderservice.model.Cart;
import com.george.orderservice.model.OrderLineItem;
import com.george.orderservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final CartRepository cartRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String,CartDto> cartDtoKafkaTemplate;
    private final KafkaTemplate<String,StripeForm> stripeFormKafkaTemplate;


    public String placeOrder(String userId){
        try{
            Cart cart = cartRepository.findByUserId(userId).orElseThrow(()->new CartNotFoundException(userId));
            CartDto cartDto = mapToCartDto(cart);
            if (cartDto.getOrderLineItemDtoList().size()==0){
                throw new Exception("Empty cart can't be checked out");
            }
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


    private CartDto mapToCartDto(Cart cart){
        CartDto cartDto = CartDto.builder().id(cart.getId()).userId(cart.getUserId()).build();
        List<OrderLineItemDto> orderLineItemDtoList = cart.getOrderLineItemList().stream().map(this::mapToOrderLineItemDto).toList();
        cartDto.setOrderLineItemDtoList(orderLineItemDtoList);
        return cartDto;
    }

    private OrderLineItemDto mapToOrderLineItemDto(OrderLineItem orderLineItem) {
        return OrderLineItemDto.builder()
                .id(orderLineItem.getId())
                .productId(orderLineItem.getProductId())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }

}
