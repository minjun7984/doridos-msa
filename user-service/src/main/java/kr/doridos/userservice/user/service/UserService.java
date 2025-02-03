package kr.doridos.userservice.user.service;

import kr.doridos.common.exception.ErrorCode;
import kr.doridos.userservice.user.entity.User;
import kr.doridos.userservice.user.dto.NicknameUpdateRequest;
import kr.doridos.userservice.user.dto.UserInfoResponse;
import kr.doridos.userservice.user.dto.UserSignUpRequest;
import kr.doridos.userservice.user.exception.NicknameAlreadyExistsException;
import kr.doridos.userservice.user.exception.UserAlreadySignUpException;
import kr.doridos.userservice.user.exception.UserNotFoundException;
import kr.doridos.userservice.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long signUp(final UserSignUpRequest userSignUpRequest) {
        validateDuplicateByEmail(userSignUpRequest.getEmail());
        validateDuplicateByNickname(userSignUpRequest.getNickname());

        final User savedUser = userRepository.save(userSignUpRequest.toEntity());
        return savedUser.getId();
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(final User user) {
        return UserInfoResponse.of(user);
    }

    public void updateNickname(final NicknameUpdateRequest request, final String email) {
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        validateUpdateByNickname(request.getNickname(), user.getNickname());
        user.updateNickname(request.getNickname());
    }

    private void validateDuplicateByEmail(final String email) {
        if (userRepository.existsByEmail(email))
            throw new UserAlreadySignUpException(ErrorCode.USER_ALREADY_SIGNUP);
    }

    private void validateDuplicateByNickname(final String nickname) {
        if (userRepository.existsByNickname(nickname))
            throw new NicknameAlreadyExistsException(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }

    private void validateUpdateByNickname(final String nickname, final String updateNickname) {
        if(nickname.equals(updateNickname))
            throw new NicknameAlreadyExistsException(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }
}

