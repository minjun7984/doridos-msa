package kr.doridos.common.auth;

import lombok.Getter;

@Getter
public class UserInfo {
    private final Long userId;
    private final String userType;

    public UserInfo(Long userId, String userType) {
        this.userId = userId;
        this.userType = userType;
    }
}
