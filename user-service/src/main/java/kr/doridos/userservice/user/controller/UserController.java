package kr.doridos.userservice.user.controller;

import jakarta.validation.Valid;
import kr.doridos.userservice.auth.support.jwt.UserDetailsImpl;
import kr.doridos.userservice.user.dto.NicknameUpdateRequest;
import kr.doridos.userservice.user.dto.UserInfoResponse;
import kr.doridos.userservice.user.dto.UserSignUpRequest;
import kr.doridos.userservice.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    public final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid final UserSignUpRequest userSignUpRequest) {
        final Long id = userService.signUp(userSignUpRequest);
        return ResponseEntity.created(URI.create("/users/me")).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal final UserDetailsImpl userDetails) {
        final UserInfoResponse response = userService.getUserInfo(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<Void> changeNickname(@RequestBody @Valid final NicknameUpdateRequest nicknameRequest,
                                               @AuthenticationPrincipal final UserDetailsImpl userDetails) {
        userService.updateNickname(nicknameRequest, userDetails.getEmail());
        return ResponseEntity.noContent().build();
    }
}
