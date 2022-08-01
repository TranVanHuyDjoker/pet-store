package com.hivetech.repository;

import com.hivetech.model.entity.Order;
import com.hivetech.model.entity.refund.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RefundRepo extends JpaRepository<Refund, Long> {
    @Query(value = "SELECT r from Refund r " +
            "inner join  Order o on o.id = r.order.id " +
            "inner join User u on u.id = o.user.id " +
            "WHERE u.id = :id")
    List<Refund> findByOrderAndMember(@Param("id") long id);
    Refund findByOrder(Order order);
}
