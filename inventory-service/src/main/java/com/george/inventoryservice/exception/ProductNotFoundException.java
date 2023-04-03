package com.george.inventoryservice.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductNotFoundException extends Exception{
    private String productId;

    @Override
    public String getMessage(){
        return String.format("Product %s not found",productId);
    }
}
