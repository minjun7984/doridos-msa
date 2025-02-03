package kr.doridos.userservice.auth.oauth;

import kr.doridos.userservice.user.entity.User;
import lombok.Getter;

@Getter
public class UserProfile {

    private final String email;
    private final String nickname;

    public UserProfile(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public User toEntity() {
        return User.ofSocial(email, nickname);
    }
}
