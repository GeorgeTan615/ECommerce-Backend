package com.george.cartservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="Cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<OrderLineItem> orderLineItemList;

}
