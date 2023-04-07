package com.george.inventoryservice.controller;

import com.george.inventoryservice.dto.InventoryRequest;
import com.george.inventoryservice.dto.InventoryResponse;
import com.george.inventoryservice.exception.ProductNotFoundException;
import com.george.inventoryservice.model.Inventory;
import com.george.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Inventory> getProductsInventory(@RequestParam List<String> productIds){
        return inventoryService.getProductsInventory(productIds);

    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createStock(@RequestBody InventoryRequest inventoryRequest){
        Inventory inventory = Inventory.builder()
                .productId(inventoryRequest.getProductId())
                .quantity(inventoryRequest.getQuantity())
                .build();
        inventoryService.createStock(inventory);

    }


    @PatchMapping("/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateStock(@PathVariable String productId, @RequestBody int newQuantity) throws ProductNotFoundException {
        inventoryService.updateStock(productId,newQuantity);

    }

    @PatchMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String updateStocks(@RequestBody List<Inventory> newInventory) throws Exception {
        return inventoryService.updateStocks(newInventory);

    }
}
