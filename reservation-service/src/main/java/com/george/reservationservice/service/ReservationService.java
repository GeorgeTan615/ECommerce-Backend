package com.george.reservationservice.service;

import com.george.reservationservice.dto.ReservationDto;
import com.george.reservationservice.exception.CartNotFoundException;
import com.george.reservationservice.model.Cart;
import com.george.reservationservice.repository.CartRepository;
import com.george.reservationservice.repository.ReservationRepository;
import com.george.reservationservice.dto.CartDto;
import com.george.reservationservice.dto.OrderLineItemDto;
import com.george.reservationservice.model.Inventory;
import com.george.reservationservice.model.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final WebClient.Builder webClientBuilder;
    private final ReservationRepository reservationRepository;
    private final CartRepository cartRepository;
    private final KafkaTemplate<String,ReservationDto> kafkaTemplate;

    @Transactional(isolation = Isolation.SERIALIZABLE,rollbackFor = Exception.class)
    public void validateAndCreateReservation(CartDto cartDto) throws Exception {
        // Create a hashmap based on items in cartDto
        HashMap<String,Integer> cartProductsQuantity = new HashMap<String,Integer>();
        List<String> productIds = cartDto.getOrderLineItemDtoList()
                                        .stream()
                                        .map(OrderLineItemDto::getProductId)
                                        .toList();
        for (OrderLineItemDto orderLineItemDto:cartDto.getOrderLineItemDtoList()){
            cartProductsQuantity.put(orderLineItemDto.getProductId(), orderLineItemDto.getQuantity());
        }
        // send request to get all related products from inventory service
        Inventory[] productsInventory = webClientBuilder.build()
                .get()
                .uri("http://inventory-service/api/inventories",
                        uriBuilder -> uriBuilder.queryParam("productIds",productIds).build()
                )
                .retrieve()
                .bodyToMono(Inventory[].class)
                .block();

        if (productsInventory == null || productsInventory.length == 0){
            throw new Exception("Empty cart can't be checked out");
        }
        if (cartProductsQuantity.size() != productsInventory.length){
            throw new Exception("Inventory for some products does not exist");
        }
        // Loop through the products obtained from inventory service and decrement the quantity accordingly
        for (Inventory inventory:productsInventory){
            int newQuantity = inventory.getQuantity() - cartProductsQuantity.get(inventory.getProductId());
            if (newQuantity<0){
                throw new Exception("Product "+ inventory.getProductId() + " does not have enough stock for user");
            }
            else{
                inventory.setQuantity(newQuantity);
            }
        }
        // send it back to inventory service to update the stock
        // Exception might be raised in the process which will cause rollback
        String response = webClientBuilder.build()
                .patch()
                .uri("http://inventory-service/api/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productsInventory)
                .retrieve()
                .bodyToMono(String.class)
                .block();


        Cart cart = cartRepository.findByUserId(cartDto.getUserId()).orElseThrow(()->new CartNotFoundException(cartDto.getUserId()));
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID().toString())
                .userId(cartDto.getUserId())
                .expirationDateTime(LocalDateTime.now().plusMinutes(10))
                .cart(cart)
                .build();
        // create reservation and publish to kafka topic
        ReservationDto reservationDto = ReservationDto.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .expirationDateTime(reservation.getExpirationDateTime())
                .cartDto(cartDto)
                .build();

        reservationRepository.save(reservation);
        kafkaTemplate.send("ordersReservedTopic",reservationDto);
    }
}
