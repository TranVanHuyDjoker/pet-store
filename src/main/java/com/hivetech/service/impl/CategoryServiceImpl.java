package com.hivetech.service.impl;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.config.exception.NotFoundException;
import com.hivetech.model.dto.CategoryDTO;
import com.hivetech.model.entity.Category;
import com.hivetech.model.request.CategoryRequest;
import com.hivetech.repository.CategoryRepo;
import com.hivetech.service.CategoryService;
import com.hivetech.utils.StringUtils;
import com.hivetech.utils.constants.CategoryConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<CategoryDTO> getCategories() {
        List<Category> categories = categoryRepo.findAll();
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        return recursiveCategories(categories, categoryDTOList, 0, "");
    }

    @Override
    public CategoryDTO getCategoryById(int id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(CategoryConstants.NOT_FOUND));
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO insertCategory(CategoryRequest request) {
        if (categoryRepo.existsByName(request.getName())) {
            throw new BadRequestException(CategoryConstants.EXISTS_BY_NAME);
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setSlug(StringUtils.makeSlug(request.getName()));
        category = categoryRepo.save(category);
        log.info("Insert category success}", category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(int id, CategoryRequest request) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(CategoryConstants.NOT_FOUND));
        if (category.getId() == request.getParentId()) throw new BadRequestException(CategoryConstants.PARENT_ID);
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setSlug(StringUtils.makeSlug(request.getName()));
        category = categoryRepo.save(category);
        log.info("Update category success", category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategoryById(int id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(CategoryConstants.NOT_FOUND));
        categoryRepo.deleteById(category.getId());
        log.info("Delete category success", category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    private List<CategoryDTO> recursiveCategories(List<Category> categories, List<CategoryDTO> categoriesDTO, int parentId, String level) {
        List<Category> categoriesAfterFilter = categories.stream()
                .filter(category -> category.getParentId() == parentId)
                .collect(Collectors.toList());
        if (categoriesDTO.size() == categories.size()) {
            return categoriesDTO;
        }
        if (categoriesAfterFilter.isEmpty()) {
            return categoriesDTO;
        }
        for (Category category : categoriesAfterFilter) {
            CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
            dto.setName(level + dto.getName());
            categoriesDTO.add(dto);
            recursiveCategories(categories, categoriesDTO, category.getId(), "-" + level);
        }
        return categoriesDTO;
    }
}

