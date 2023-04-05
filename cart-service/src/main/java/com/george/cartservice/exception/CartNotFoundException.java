package com.george.cartservice.exception;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CartNotFoundException extends Exception {
    private String userId;

    public String getMessage(){
        return "Cart for " + userId + " is not found";
    }
}
