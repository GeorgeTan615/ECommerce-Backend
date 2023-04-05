package com.george.cartservice.controller;


import com.george.cartservice.dto.CartResponse;
import com.george.cartservice.dto.OrderLineItemRequest;
import com.george.cartservice.exception.CartNotFoundException;
import com.george.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Get all cart items
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCart(@PathVariable String userId) throws CartNotFoundException {
        return cartService.getCart(userId);
    }

    // Add a new item to cart or create new cart
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addCartItem(@PathVariable String userId,
                                @RequestBody OrderLineItemRequest orderLineItemRequest
                            ) throws CartNotFoundException {
        return cartService.addCartItem(userId,orderLineItemRequest);
    }

    //Create cart for user
    @PostMapping("/{userId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public String createCart(@PathVariable String userId
    ) {

        return cartService.createCart(userId);

    }

    // Update existing item in cart
    @PutMapping("/{userId}/{orderLineItemId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateCartItem(@PathVariable String userId,
                                @PathVariable Long orderLineItemId,
                                @RequestBody OrderLineItemRequest orderLineItemRequest){
        return cartService.updateCartItem(userId,orderLineItemId,orderLineItemRequest);
    }

    // Delete existing item in cart
    @DeleteMapping("/{userId}/{orderLineItemId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCartItem(@PathVariable String userId,
                                       @PathVariable Long orderLineItemId){
        return cartService.deleteCartItem(userId,orderLineItemId);

    }

    @PostMapping("/{userId}/checkout")
//    @ResponseStatus(HttpStatus.OK)
    public String checkOutCart(@PathVariable String userId){
        return cartService.checkOut(userId);

    }
}
