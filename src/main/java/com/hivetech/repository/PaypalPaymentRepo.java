package com.hivetech.repository;

import com.hivetech.model.entity.Order;
import com.hivetech.model.entity.PaypalPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaypalPaymentRepo extends JpaRepository<PaypalPayment, String> {
    Optional<PaypalPayment> findById(String paymentId);

    Optional<PaypalPayment> findBySaleId(String saleId);
    Optional<PaypalPayment> findByOrder(Order order);
}
