package com.george.orderservice.dto;

import com.george.orderservice.model.OrderLineItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long id;
    private String userId;
    private List<OrderLineItemDto> orderLineItemDtoList;
}
