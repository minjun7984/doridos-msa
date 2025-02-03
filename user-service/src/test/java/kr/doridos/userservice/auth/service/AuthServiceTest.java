package kr.doridos.userservice.auth.service;

import kr.doridos.userservice.auth.dto.SignInRequest;
import kr.doridos.userservice.auth.dto.SignInResponse;
import kr.doridos.userservice.auth.exception.SignInFailureException;
import kr.doridos.userservice.auth.support.jwt.JwtProvider;
import kr.doridos.userservice.user.entity.EncodedPassword;
import kr.doridos.userservice.user.entity.User;
import kr.doridos.userservice.user.entity.UserType;
import kr.doridos.userservice.user.fixture.UserFixture;
import kr.doridos.userservice.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    private SignInRequest signInRequest;

    @BeforeEach
    public void setUp() {
        signInRequest = new SignInRequest("test@test.com", "12345678a!");
    }

    @Test
    void 입력정보가_일치하면_토큰을_발급한다() {
        User user = User.builder().id(1L)
                .password(EncodedPassword.encode("12345678a!"))
                .nickname("두루리루").phoneNumber("01012345432")
                .userType(UserType.USER)
                .build();

        given(userRepository.findByEmail(signInRequest.getEmail())).willReturn(Optional.of(user));
        given(jwtProvider.createAccessToken("test@test.com", UserType.USER, 1L)).willReturn("accessToken");

        SignInResponse signInResponse = authService.signIn(signInRequest);

        assertThat(signInResponse.getToken()).isEqualTo("accessToken");
    }

    @Test
    void 가입된_유저가_아니면_예외를_발생한다() {
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.signIn(signInRequest))
                .isInstanceOf(SignInFailureException.class)
                .hasMessage("로그인에 실패하였습니다.");
    }

    @Test
    void 패스워드가_일치하지_않으면_예외가_발생한다() {
        User user = UserFixture.일반_유저_생성();
        SignInRequest request = new SignInRequest("test@test.com", "123456a!");

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.signIn(request))
                .isInstanceOf(SignInFailureException.class)
                .hasMessage("로그인에 실패하였습니다.");
    }
}
