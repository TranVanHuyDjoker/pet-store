package com.hivetech.repository;

import com.hivetech.model.entity.Category;
import com.hivetech.model.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepo extends JpaRepository<Pet, Long> {
    boolean existsByName(String name);

    Page<Pet> findByCategory(Category category, Pageable pageable);

    @Query(value = "SELECT p FROM Pet p "
            + " WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    Page<Pet> paging(@Param("keyword") String keyword, Pageable pageable);

    Pet findByName(String name);
}
