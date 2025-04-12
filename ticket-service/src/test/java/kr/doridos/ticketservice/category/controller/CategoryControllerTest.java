package kr.doridos.ticketservice.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.doridos.ticketservice.category.dto.CategoryRequest;
import kr.doridos.ticketservice.category.entity.Category;
import kr.doridos.ticketservice.category.repository.CategoryRepository;
import kr.doridos.ticketservice.category.service.CategoryService;
import kr.doridos.ticketservice.ticket.fixture.CategoryFixture;
import kr.doridos.ticketservice.ticket.fixture.TokenFixture;
import kr.doridos.ticketservice.util.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    String userToken;
    String ticketManagerToken;

    @BeforeEach
    void setUp() {
        userToken = TokenFixture.일반유저_토큰_생성();
        ticketManagerToken = TokenFixture.티켓매니저_토큰_생성();
        categoryRepository.save(CategoryFixture.카테고리_생성());
    }

    @Test
    void 카테고리_생성에_성공한다200() throws Exception {
        CategoryRequest request = new CategoryRequest("농구", 1L);

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + ticketManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("카테고리 생성",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 권한없는_사용자가_카테고리를_생성하려하면_예외가발생한다401() throws Exception {
        CategoryRequest request = new CategoryRequest("농구", 1L);

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("권한이 없는 사용자입니다."))
                .andDo(document("권한 없는 사용자 카테고리 생성",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 이미존재하는_카테고리네임으로_요쳥하면_예외가_발생한다400() throws Exception {
        CategoryRequest request = new CategoryRequest("스포츠", 1L);

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + ticketManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("카테고리가 이미 존재합니다."))
                .andDo(document("카테고리네임 중복 에러 발생",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 카테고리_조회에_성공한다200() throws Exception {
        Category parent = new Category(1L, "뮤지컬", null, new ArrayList<>());
        Category child = new Category(2L, "오페라", parent, new ArrayList<>());
        categoryRepository.save(parent);
        categoryRepository.save(child);

        mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("카테고리 조회 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}
