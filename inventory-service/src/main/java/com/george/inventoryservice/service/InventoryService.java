package com.george.inventoryservice.service;


import com.george.inventoryservice.dto.InventoryResponse;
import com.george.inventoryservice.exception.ProductNotFoundException;
import com.george.inventoryservice.model.Inventory;
import com.george.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<Inventory> getProductsInventory(List<String> productIds){
        return inventoryRepository
                .findByProductIdIn(productIds);
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String updateStocks(List<Inventory> newInventory) throws RuntimeException {
        try{
            for (Inventory inventory: newInventory){
                inventoryRepository.save(inventory);
            }
            log.info("Inventory update success");
            return "Update success";
        }
        catch (Exception e){
            throw new RuntimeException("Update new inventory failed");
        }

    }
}
