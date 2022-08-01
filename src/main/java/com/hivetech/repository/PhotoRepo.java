package com.hivetech.repository;

import com.hivetech.model.entity.Pet;
import com.hivetech.model.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepo extends JpaRepository<Photo, Integer> {
    @Query("SELECT p FROM Photo p "
            + "WHERE p.pet = ?1 "
            + "ORDER BY p.id ASC")
    List<Photo> findByPet(Pet pet);

    boolean existsByHashingCodeMD5(String MD5Code);

    @Query("SELECT p FROM Photo p "
            + "WHERE p.primaryPhoto = TRUE AND p.pet = :pet")
    Photo findPrimaryPhotoByPet(Pet pet);
}
