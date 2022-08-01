package com.hivetech.repository;

import com.hivetech.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);

    Optional<Category> findBySlug(String slug);
}
