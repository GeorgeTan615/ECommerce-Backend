package com.george.cartservice.dto;

import com.george.cartservice.model.Cart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemResponse {
    private Long id;
    private String productId;
    private BigDecimal price;
    private Integer quantity;
}
