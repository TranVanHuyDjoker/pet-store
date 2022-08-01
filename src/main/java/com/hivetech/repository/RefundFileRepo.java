package com.hivetech.repository;

import com.hivetech.model.entity.refund.RefundFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundFileRepo extends JpaRepository<RefundFile,Long> {
}
