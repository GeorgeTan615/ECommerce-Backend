package com.george.cartservice.controller;


import com.george.cartservice.dto.CartResponse;
import com.george.cartservice.dto.OrderLineItemRequest;
import jakarta.ws.rs.Path;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    // Get all cart items
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCart(@PathVariable String userId){


    }

    // Add a new item to cart
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addCartItem(@PathVariable String userId,
                                @RequestBody OrderLineItemRequest orderLineItemRequest){


    }

    // Update existing item in cart
    @PutMapping("/{userId}/{orderLineItemId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse updateCartItem(@PathVariable String userId,
                                @PathVariable Long orderLineItemId,
                                @RequestBody OrderLineItemRequest orderLineItemRequest){


    }

    // Delete existing item in cart
    @DeleteMapping("/{userId}/{orderLineItemId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse deleteCartItem(@PathVariable String userId,
                                       @PathVariable Long orderLineItemId){


    }

    @PostMapping("/{userId}/checkout")
    @ResponseStatus(HttpStatus.OK)
    public void checkOutCart(@PathVariable String userId){

    }
}
