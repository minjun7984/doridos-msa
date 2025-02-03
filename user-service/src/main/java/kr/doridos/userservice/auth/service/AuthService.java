package kr.doridos.userservice.auth.service;

import kr.doridos.common.exception.ErrorCode;
import kr.doridos.userservice.auth.dto.SignInRequest;
import kr.doridos.userservice.auth.dto.SignInResponse;
import kr.doridos.userservice.auth.exception.SignInFailureException;
import kr.doridos.userservice.auth.support.jwt.JwtProvider;
import kr.doridos.userservice.user.entity.EncodedPassword;
import kr.doridos.userservice.user.entity.User;
import kr.doridos.userservice.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public AuthService(final UserRepository userRepository, final JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public SignInResponse signIn(final SignInRequest signInRequest) {
        final User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new SignInFailureException(ErrorCode.SIGN_IN_FAIL));

        if (!EncodedPassword.matches(signInRequest.getPassword(), user.getPassword()))
            throw new SignInFailureException(ErrorCode.SIGN_IN_FAIL);

        String token = jwtProvider.createAccessToken(signInRequest.getEmail(), user.getUserType(), user.getId());
        return new SignInResponse(token);
    }
}
