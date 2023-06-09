package com.george.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Inventory")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    private String productId;
    private Integer quantity;

}
