package com.george.inventoryservice.repository;

import com.george.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,String> {
    Optional<Inventory> findByProductId(String productId);

    List<Inventory> findByProductIdIn(List<String> productId);
}
