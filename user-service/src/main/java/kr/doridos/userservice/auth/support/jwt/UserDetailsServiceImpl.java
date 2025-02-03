package kr.doridos.userservice.auth.support.jwt;

import kr.doridos.common.exception.ErrorCode;
import kr.doridos.userservice.user.entity.User;
import kr.doridos.userservice.user.exception.UserNotFoundException;
import kr.doridos.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        return new UserDetailsImpl(user, user.getEmail());
    }
}
