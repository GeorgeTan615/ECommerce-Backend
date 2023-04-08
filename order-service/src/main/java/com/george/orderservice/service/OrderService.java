package com.george.orderservice.service;

import com.george.orderservice.dto.*;
import com.george.orderservice.exception.CartNotFoundException;
import com.george.orderservice.model.Cart;
import com.george.orderservice.model.Order;
import com.george.orderservice.model.OrderLineItem;
import com.george.orderservice.repository.CartRepository;
import com.george.orderservice.repository.OrderRepository;
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
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String,CartDto> cartDtoKafkaTemplate;
    private final KafkaTemplate<String,StripeForm> stripeFormKafkaTemplate;

    public List<OrderResponse> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapOrderToOrderResponse).toList();
    }

    private OrderResponse mapOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(order.getOrderNumber());

        orderResponse.setOrderLineItemsDtoList(
                order.getOrderLineItems()
                        .stream()
                        .map(this::mapToOrderLineItemDto)
                        .toList()
        );
        return orderResponse;
    }

    private OrderLineItemDto mapToOrderLineItemDto(OrderLineItem orderLineItem) {
        return OrderLineItemDto.builder()
                .id(orderLineItem.getId())
                .productId(orderLineItem.getProductId())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }

    public String placeOrder(String userId){
        try{
            Cart cart = cartRepository.findByUserId(userId).orElseThrow(()->new CartNotFoundException(userId));
            CartDto cartDto = mapToCartDto(cart);
            if (cartDto.getOrderLineItemDtoList().size()==0){
                throw new Exception("Empty cart can't be checked out");
            }
////        Order order = new Order();
////        order.setOrderNumber(UUID.randomUUID().toString());
//
////            List<OrderLineItem> orderLineItems = orderRequest
////                    .getOrderLineItemsDtoList()
////                    .stream()
////                    .map(this::mapToOrderLineItem)
////                    .toList();
////
////            order.setOrderLineItems(orderLineItems);
////
////            List<String> skuCodes = orderLineItems.stream().map(OrderLineItem::getSkuCode).toList();
////            // Call inventory service and place order if product is in stock
////            InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
////                    .uri("http://inventory-service/api/inventories",
////                            uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
////                    .retrieve()
////                    .bodyToMono(InventoryResponse[].class)
////                    .block();
////
////            boolean allProductsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
////            if (allProductsInStock){
////                orderRepository.save(order);
            cartDtoKafkaTemplate.send("ordersPlacedTopic",cartDto);
//            log.info(cart.getUserId());
            log.info("Message sent to kafka topic");
            return "Order placed successfully!";
//            }
//            else{
//                throw new IllegalArgumentException("Product is not in stock");
//            }
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

    private OrderLineItem mapToOrderLineItem(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .productId(orderLineItemDto.getProductId())
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .build();
    }

    private CartDto mapToCartDto(Cart cart){
        CartDto cartDto = CartDto.builder().id(cart.getId()).userId(cart.getUserId()).build();
        List<OrderLineItemDto> orderLineItemDtoList = cart.getOrderLineItemList().stream().map(this::mapToOrderLineItemDto).toList();
        cartDto.setOrderLineItemDtoList(orderLineItemDtoList);
        return cartDto;
    }



}
