package com.hivetech.repository;

import com.hivetech.model.entity.Delivery;
import com.hivetech.utils.enumerates.DeliveryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepo extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByName(DeliveryType name);
}
