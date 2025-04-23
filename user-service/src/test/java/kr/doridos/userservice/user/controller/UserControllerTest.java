package kr.doridos.userservice.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.doridos.userservice.auth.support.jwt.JwtProvider;
import kr.doridos.userservice.user.dto.NicknameUpdateRequest;
import kr.doridos.userservice.user.dto.UserSignUpRequest;
import kr.doridos.userservice.user.entity.User;
import kr.doridos.userservice.user.entity.UserType;
import kr.doridos.userservice.user.fixture.UserFixture;
import kr.doridos.userservice.user.repository.UserRepository;
import kr.doridos.userservice.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtProvider jwtProvider;

    private String token;

    @BeforeEach
    void setUp() {
        User user = UserFixture.일반_유저_생성();
        userRepository.save(user);
        token = jwtProvider.createAccessToken(user.getEmail(), user.getUserType(),user.getId());
    }

    @Test
    void 회원가입에_성공한다() throws Exception {
        final UserSignUpRequest userSignUpRequest = new UserSignUpRequest("test1@email.com",
                "12345678a!",
                "하루하루",
                "01012341234",
                UserType.USER);

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignUpRequest)))
                .andExpect(status().isCreated())
                .andDo(document("유저 회원가입 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 회원가입시_이메일이_중복되면_예외가_발생한다() throws Exception {
        final UserSignUpRequest userSignUpRequest = new UserSignUpRequest(UserFixture.일반_유저_생성().getEmail(),
                "12345678a!",
                "하루하루",
                "01012341234",
                UserType.USER);

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignUpRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 가입한 유저입니다."))
                .andDo(document("중복된 이메일로 회원가입",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 회원가입시_닉네임이_중복되면_예외가_발생한다() throws Exception {
        final UserSignUpRequest userSignUpRequest = new UserSignUpRequest("test@naver.com",
                "12345678a!",
                UserFixture.일반_유저_생성().getNickname(),
                "01012341234",
                UserType.USER);

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignUpRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 존재하는 닉네임입니다."))
                .andDo(document("중복된 닉네임으로 회원가입",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 유저정보_조회에_성공한다() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(document("유저 정보조회 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 닉네임_변경에_성공한다() throws Exception {
        NicknameUpdateRequest nicknameRequest = new NicknameUpdateRequest("도리도스");

        mockMvc.perform(patch("/api/v1/users/me/nickname")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameRequest)))
                .andExpect(status().isNoContent())
                .andDo(document("닉네임 변경 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 닉네임이_존재하면_예외를_반환한다() throws Exception {
        NicknameUpdateRequest nicknameRequest = new NicknameUpdateRequest("test");

        mockMvc.perform(patch("/api/v1/users/me/nickname")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 존재하는 닉네임입니다."))
                .andDo(document("이미 존재하는 닉네임 변경",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}
