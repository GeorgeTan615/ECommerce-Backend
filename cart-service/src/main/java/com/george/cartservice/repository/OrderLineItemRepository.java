package com.george.cartservice.repository;

import com.george.cartservice.model.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderLineItemRepository extends JpaRepository<OrderLineItem,Long> {
}
