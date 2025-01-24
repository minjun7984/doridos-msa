package kr.doridos.ticketservice.category.service;

import kr.doridos.ticketservice.category.dto.CategoryListResponse;
import kr.doridos.ticketservice.category.dto.CategoryRequest;
import kr.doridos.ticketservice.category.dto.CategoryResponse;
import kr.doridos.ticketservice.category.entity.Category;
import kr.doridos.ticketservice.category.exception.CategoryAlreadyExistsException;
import kr.doridos.ticketservice.category.repository.CategoryRepository;
import kr.doridos.ticketservice.ticket.exception.UserNotTicketManagerException;
import kr.doridos.ticketservice.ticket.fixture.CategoryFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category parentCategory;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        parentCategory = CategoryFixture.카테고리_생성();
        Category parentCategory = new Category(1L, "뮤지컬", null, new ArrayList<>());
        Category childCategory = new Category(2L, "오페라", parentCategory, new ArrayList<>());

        parentCategory.getChildren().add(childCategory);
        categories = List.of(parentCategory);
    }

    @Test
    void 카테고리_생성에_성공한다() {
        CategoryRequest request = new CategoryRequest("축구", parentCategory.getId());
        given(categoryRepository.findById(parentCategory.getId())).willReturn(Optional.of(parentCategory));

        CategoryResponse response = categoryService.createCategory("TICKET_MANAGER", request);

        assertSoftly(softly -> {
            softly.assertThat(request.getName()).isEqualTo(response.getName());
            softly.assertThat(request.getParentId()).isEqualTo(response.getParentId());
        });
    }

    @Test
    void 카테고리_생성시_티켓매니저가_아니면_예외가_발생한다() {
        CategoryRequest request = new CategoryRequest("축구", parentCategory.getId());

        assertThatThrownBy(() -> categoryService.createCategory("USER", request))
                .isInstanceOf(UserNotTicketManagerException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    void 카테고리_생성시_카테고리네임이_이미_존재하면_예외가_발생한다() {
        CategoryRequest request = new CategoryRequest(parentCategory.getName(), parentCategory.getId());
        given(categoryRepository.existsByName(parentCategory.getName())).willReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory("TICKET_MANAGER", request))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessage("카테고리가 이미 존재합니다.");
    }

    @Test
    void 카테고리를_조회에_성공한다() {
        given(categoryRepository.findAllCategories()).willReturn(categories);

        List<CategoryListResponse> response = categoryService.findAllCategories();

        CategoryListResponse parent = response.stream()
                .filter(cat -> cat.getId().equals(1L))
                .findFirst()
                .orElse(null);
        CategoryListResponse child = parent.getChildren().get(0);

        assertSoftly(softly -> {
            softly.assertThat(response).hasSize(1);
            softly.assertThat(parent).isNotNull();
            softly.assertThat(parent.getName()).isEqualTo("뮤지컬");
            softly.assertThat(parent.getChildren()).hasSize(1);
            softly.assertThat(child.getName()).isEqualTo("오페라");
        });
    }
}
