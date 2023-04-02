package com.george.inventoryservice.controller;

import com.george.inventoryservice.dto.InventoryRequest;
import com.george.inventoryservice.dto.InventoryResponse;
import com.george.inventoryservice.model.Inventory;
import com.george.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);

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
}
