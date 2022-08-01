package com.hivetech.controller;

import com.hivetech.model.dto.CategoryDTO;
import com.hivetech.model.dto.PageDTO;
import com.hivetech.model.dto.PetDTO;
import com.hivetech.model.request.CategoryRequest;
import com.hivetech.service.CategoryService;
import com.hivetech.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final PetService petService;

    //API
    @GetMapping( "/api/v1/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @GetMapping("/api/v1/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/api/v1/categories/{categorySlug}/pets")
    public ResponseEntity<PageDTO<PetDTO>> getPetsByCategory(@PathVariable String categorySlug,
                                                             @RequestParam(defaultValue = "1") int currentPage,
                                                             @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(petService.findPetByCategory(categorySlug,currentPage, limit));
    }

    @PostMapping("/api/v1/categories")
    public ResponseEntity<CategoryDTO> insertCategory(@Valid @RequestBody CategoryRequest request, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(categoryService.insertCategory(request));
    }

    @PutMapping("/api/v1/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable int id, @Valid @RequestBody CategoryRequest request, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/api/v1/categories/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.deleteCategoryById(id));
    }

    //VIEW

    @GetMapping({"/admin/categories/","/admin/categories"})
    public String findAll(Model model){
        model.addAttribute("title","Quản trị: Danh mục");
        return "category/admin/index";
    }
    @GetMapping("/admin/categories/{id}")
    public String findById(@PathVariable int id,Model model){
        model.addAttribute("title","Danh mục " + id );
        return "";
    }

    @GetMapping("/admin/categories/add")
    public String add(Model model){
        model.addAttribute("title","Tạo danh mục" );
        return "category/admin/form";
    }

    @GetMapping("/admin/categories/update")
    public String update(@RequestParam int id, Model model){
        model.addAttribute("title","Cập nhật danh mục "+ id );
        return "category/admin/form";
    }
}
