package com.george.reservationservice.service;

import com.george.reservationservice.dto.ReservationDto;
import com.george.reservationservice.dto.StripeForm;
import com.george.reservationservice.exception.CartNotFoundException;
import com.george.reservationservice.model.Cart;
import com.george.reservationservice.repository.CartRepository;
import com.george.reservationservice.repository.InventoryRepository;
import com.george.reservationservice.repository.ReservationRepository;
import com.george.reservationservice.dto.CartDto;
import com.george.reservationservice.dto.OrderLineItemDto;
import com.george.reservationservice.model.Inventory;
import com.george.reservationservice.model.Reservation;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final WebClient.Builder webClientBuilder;
    private final ReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;
    private final KafkaTemplate<String,ReservationDto> kafkaTemplate;

    // Using read commited instead of default repeatable read
    // Because consistent reading is not valuable to us
    // Recent updates are more valuable to us since we are handling inventory
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = {
                                                            RuntimeException.class,
                                                            LockTimeoutException.class})
    public void validateAndCreateReservation(CartDto cartDto) throws RuntimeException {
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
        // This is skipped for now as we want to maintain in the same Transaction
//        Inventory[] productsInventory = webClientBuilder.build()
//                .get()
//                .uri("http://inventory-service/api/inventories",
//                        uriBuilder -> uriBuilder.queryParam("productIds",productIds).build()
//                )
//                .retrieve()
//                .bodyToMono(Inventory[].class)
//                .block();
        List<Inventory> productsInventory = inventoryRepository.findByProductIdIn(productIds);

        if (productsInventory == null || productsInventory.size() == 0){
            throw new RuntimeException("Empty cart can't be checked out");
        }
        if (cartProductsQuantity.size() != productsInventory.size()){
            throw new RuntimeException("Inventory for some products does not exist");
        }
        // Loop through the products obtained from inventory service and decrement the quantity accordingly
        for (Inventory inventory:productsInventory){
            int newQuantity = inventory.getQuantity() - cartProductsQuantity.get(inventory.getProductId());
            if (newQuantity<0){
                throw new RuntimeException("Product "+ inventory.getProductId() + " does not have enough stock for user");
            }
            else{
                inventory.setQuantity(newQuantity);
            }
        }
        // send it back to inventory service to update the stock
        // If want to rollback updates done using HTTP request, need more configurations

//        String response = webClientBuilder.build()
//                .patch()
//                .uri("http://inventory-service/api/inventories")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(productsInventory)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        if (response != null){
//            throw new RuntimeException("test if rollback works");
//        }

        try{
            for (Inventory productInventory: productsInventory){
                inventoryRepository.save(productInventory);
            }
        }
        catch (Exception e){
            throw new RuntimeException("Update new inventory failed");
        }
        log.info("Inventory update success");


        Cart cart = cartRepository.findByUserId(cartDto.getUserId()).orElseThrow(()->new RuntimeException("Cart not found"));
//        if (!UUID.randomUUID().toString().equals("hello")){
//            throw new RuntimeException("test if rollback works");
//
//        }
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

    @Transactional
    public void removeStaleReservations() {
//        LocalDateTime cutOffTime = LocalDateTime.now().minusMinutes(10);
        List<Reservation> staleReservations = reservationRepository.findAllByExpirationDateTimeBefore(LocalDateTime.now());

        // Collect the quantity to restore back for each product
        HashMap<String,Integer> productsRestoreQuantity = new HashMap<>();

        staleReservations.forEach(staleReservation ->
                staleReservation.getCart()
                        .getOrderLineItemList()
                        .forEach(orderLineItem -> {
                            String productId = orderLineItem.getProductId();
                            Integer initialQuantity = productsRestoreQuantity.get(productId) != null
                                                    ? productsRestoreQuantity.get(productId) : 0;
                            productsRestoreQuantity.put(productId,initialQuantity+ orderLineItem.getQuantity());
                                }
                        ));
        List<String> productIds = new ArrayList<String>(productsRestoreQuantity.keySet());

        List<Inventory> productsInventory = inventoryRepository.findByProductIdIn(productIds);

        productsInventory.forEach(product ->
                product.setQuantity(product.getQuantity() +
                        productsRestoreQuantity.get(product.getProductId()))
        );

        inventoryRepository.saveAll(productsInventory);
        reservationRepository.deleteAll(staleReservations);
        log.info("Removed stale reservations if any...");
    }

    public void removePaidReservations(StripeForm stripeForm){
        reservationRepository.deleteById(stripeForm.getReservationDto().getId());
    }
}
