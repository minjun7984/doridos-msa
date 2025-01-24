package kr.doridos.ticketservice.category.service;

import kr.doridos.common.exception.ErrorCode;
import kr.doridos.ticketservice.category.dto.CategoryListResponse;
import kr.doridos.ticketservice.category.dto.CategoryRequest;
import kr.doridos.ticketservice.category.dto.CategoryResponse;
import kr.doridos.ticketservice.category.entity.Category;
import kr.doridos.ticketservice.category.exception.CategoryAlreadyExistsException;
import kr.doridos.ticketservice.category.exception.CategoryNotFoundException;
import kr.doridos.ticketservice.category.repository.CategoryRepository;
import kr.doridos.ticketservice.ticket.exception.UserNotTicketManagerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(final String userType, final CategoryRequest categoryRequest) {
        validateUserType(userType);
        validateCategoryName(categoryRequest.getName());

        final Category category = categoryRepository.findById(categoryRequest.getParentId())
                .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

        final Category createCategory = Category.builder()
                .name(categoryRequest.getName())
                .parent(category)
                .build();

        categoryRepository.save(createCategory);
        return CategoryResponse.of(createCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryListResponse> findAllCategories() {
        List<Category> response = categoryRepository.findAllCategories();

        return response.stream()
                .map(CategoryListResponse::of)
                .collect(Collectors.toList());
    }

    private void validateCategoryName(final String categoryName) {
        if (categoryRepository.existsByName(categoryName))
            throw new CategoryAlreadyExistsException(ErrorCode.CATEGORY_EXIST);
    }

    private void validateUserType(final String userType) {
        if (!userType.equals("TICKET_MANAGER"))
            throw new UserNotTicketManagerException(ErrorCode.NOT_TICKET_MANAGER);
    }
}
