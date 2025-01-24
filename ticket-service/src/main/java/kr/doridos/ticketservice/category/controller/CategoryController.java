package kr.doridos.ticketservice.category.controller;

import kr.doridos.common.auth.AuthUser;
import kr.doridos.common.auth.UserInfo;
import kr.doridos.ticketservice.category.dto.CategoryListResponse;
import kr.doridos.ticketservice.category.dto.CategoryRequest;
import kr.doridos.ticketservice.category.dto.CategoryResponse;
import kr.doridos.ticketservice.category.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@AuthUser final UserInfo userInfo,
                                                           @RequestBody final CategoryRequest categoryRequest) {
        CategoryResponse response = categoryService.createCategory(userInfo.getUserType(), categoryRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryListResponse>> findAllCategories() {
        List<CategoryListResponse> categoryListResponses = categoryService.findAllCategories();
        return ResponseEntity.ok(categoryListResponses);
    }
}
