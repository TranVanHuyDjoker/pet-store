package com.hivetech.service;

import com.hivetech.model.dto.CategoryDTO;
import com.hivetech.model.request.CategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getCategories();

    CategoryDTO getCategoryById(int id);

    CategoryDTO insertCategory(CategoryRequest request);

    CategoryDTO updateCategory(int id, CategoryRequest request);

    CategoryDTO deleteCategoryById(int id);
}
