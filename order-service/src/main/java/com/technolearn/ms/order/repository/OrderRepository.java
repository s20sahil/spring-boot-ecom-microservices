package com.technolearn.ms.order.repository;

import com.technolearn.ms.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}