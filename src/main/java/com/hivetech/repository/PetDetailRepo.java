package com.hivetech.repository;

import com.hivetech.model.entity.Pet;
import com.hivetech.model.entity.PetDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetDetailRepo extends JpaRepository<PetDetail, Long> {
    Optional<PetDetail> findByPet(Pet pet);
}
