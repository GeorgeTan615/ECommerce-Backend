package com.george.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private String userId;
    private List<OrderLineItemResponse> orderLineItemResponseList;
}
