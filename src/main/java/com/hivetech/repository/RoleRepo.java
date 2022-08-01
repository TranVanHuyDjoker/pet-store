package com.hivetech.repository;

import com.hivetech.model.entity.Role;
import com.hivetech.utils.enumerates.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    boolean existsByName(RoleType roleType);

    Optional<Role> findByName(RoleType name);
}
