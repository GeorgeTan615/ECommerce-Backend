package com.george.inventoryservice.service;


import com.george.inventoryservice.dto.InventoryResponse;
import com.george.inventoryservice.exception.ProductNotFoundException;
import com.george.inventoryservice.model.Inventory;
import com.george.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> productId){
//        log.info("Wait started");
//        Thread.sleep(10000);
//        log.info("Wait ended");
        return inventoryRepository
                .findByProductIdIn(productId)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                        .productId(inventory.getProductId())
                        .isInStock(inventory.getQuantity()>0)
                        .build()
                ).toList();
    }

    @Transactional
    public void createStock(Inventory inventory){
        inventoryRepository.save(inventory);
        log.info("Inventory created");

    }

    @Transactional
    public void updateStock(String productId,int newQuantity) throws ProductNotFoundException {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(()->new ProductNotFoundException(productId));
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);

    }
}
