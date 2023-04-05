package com.george.cartservice.service;

import com.george.cartservice.dto.CartResponse;
import com.george.cartservice.dto.OrderLineItemRequest;
import com.george.cartservice.dto.OrderLineItemResponse;
import com.george.cartservice.exception.CartNotFoundException;
import com.george.cartservice.model.Cart;
import com.george.cartservice.model.OrderLineItem;
import com.george.cartservice.repository.CartRepository;
import com.george.cartservice.repository.OrderLineItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final WebClient.Builder webClientBuilder;

    public String createCart(String userId){
        try{
            Cart cart = Cart.builder()
                    .userId(userId)
                    .orderLineItemList(new ArrayList<>())
                    .build();
            cartRepository.save(cart);
            return "Cart successfully created for " + userId;
        }
        catch(Exception e){
            return e.getMessage();
        }
    }
    public CartResponse getCart(String userId) throws CartNotFoundException {
        Cart cart =  cartRepository.findByUserId(userId)
                .orElseThrow(()->new CartNotFoundException(userId));
        CartResponse cartResponse = CartResponse.builder()
                                                .userId(userId)
                                                .build();
        List<OrderLineItemResponse> orderLineItemResponseList = cart.getOrderLineItemList()
                                        .stream()
                                        .map(this::mapToOrderLineItemResponse)
                                        .toList();
        cartResponse.setOrderLineItemResponseList(orderLineItemResponseList);
        return cartResponse;
    }

    public OrderLineItemResponse mapToOrderLineItemResponse(OrderLineItem orderLineItem){
        return OrderLineItemResponse.builder()
                .id(orderLineItem.getId())
                .productId(orderLineItem.getProductId())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }

    public String addCartItem(String userId, OrderLineItemRequest orderLineItemRequest){
        String res = "";
        try{
            OrderLineItem orderLineItem = OrderLineItem.builder()
                    .productId(orderLineItemRequest.getProductId())
                    .price(orderLineItemRequest.getPrice())
                    .quantity(orderLineItemRequest.getQuantity())
                    .build();
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(()->new CartNotFoundException(userId));
            orderLineItem.setCart(cart);
            orderLineItemRepository.save(orderLineItem);
            cartRepository.save(cart);
            res = "Item successfully added to cart";
        }
        catch (Exception e){
            res = e.getMessage();

        }
        return res;
    }

    public String updateCartItem(String userId, Long orderLineItemId, OrderLineItemRequest orderLineItemRequest) {
        String res;
        try{
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(()->new CartNotFoundException(userId));
            List<OrderLineItem> orderLineItemList = cart.getOrderLineItemList();
            OrderLineItem orderLineItem = orderLineItemList.stream()
                                            .filter(item -> item.getId().equals(orderLineItemId))
                                            .findFirst()
                                            .orElse(null);
            if (orderLineItem == null){
                throw new Exception("Order line item does not exist in cart");
            }
            orderLineItem.setPrice(orderLineItemRequest.getPrice());
            orderLineItem.setQuantity(orderLineItemRequest.getQuantity());
            cart.setOrderLineItemList(orderLineItemList);
            orderLineItemRepository.save(orderLineItem);
            cartRepository.save(cart);
            res = "Cart item successfully updated";
        }
        catch (Exception e){
            res = e.getMessage();
        }
        return res;

    }

    public String deleteCartItem(String userId, Long orderLineItemId) {
        String res;
        try{
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(()->new CartNotFoundException(userId));
            OrderLineItem orderLineItem = orderLineItemRepository.findById(orderLineItemId)
                    .orElseThrow(()->new Exception("Order line item not found"));

            List<OrderLineItem> orderLineItemList = cart.getOrderLineItemList();

            orderLineItemList.removeIf(item -> item.equals(orderLineItem));

            cart.setOrderLineItemList(orderLineItemList);
            cartRepository.save(cart);
            orderLineItemRepository.delete(orderLineItem);
            res = "Cart item successfully deleted";

        }catch (Exception e){
            res = e.getMessage();

        }
        return res;
    }

    public String checkOut(String userId) {
        try{
//            Cart cart = cartRepository.findByUserId(userId).orElseThrow(()->new CartNotFoundException(userId));
            webClientBuilder.build()
                    .post()
                    .uri("http://order-service/api/orders")
                    .bodyValue(userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(result -> {
                        log.info("User {} is checking out",userId);
                    });

            return "User " + userId + " is checking out";
        }
        catch (Exception e){
            return e.getMessage();
        }

    }
}
