package com.george.orderservice.dto;

import com.george.orderservice.model.Cart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemDto {
    private Long id;
    private String productId;
    private BigDecimal price;
    private Integer quantity;
}
