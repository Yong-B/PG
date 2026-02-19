package com.example.PG.purchase.repository;

import com.example.PG.purchase.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMerchantUid(String merchantUid);
}

