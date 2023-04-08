package com.george.reservationservice.repository;

import com.george.reservationservice.model.Inventory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,String> {
    Optional<Inventory> findByProductId(String productId);

    // We use pessimistic write to prevent other transactions from reading and updating this row
    // We only lock the rows involved
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    List<Inventory> findByProductIdIn(List<String> productId);

}
