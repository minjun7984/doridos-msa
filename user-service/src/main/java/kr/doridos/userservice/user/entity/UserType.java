package kr.doridos.userservice.user.entity;

import lombok.Getter;

@Getter
public enum UserType {
    TICKET_MANAGER(Authority.TICKET_MANAGER),
    USER(Authority.USER),
    SOCIAL(Authority.SOCIAL);

    private final String authority;

    UserType(final String authority) {
        this.authority = authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String TICKET_MANAGER = "ROLE_TICKET_MANAGER";
        public static final String SOCIAL = "ROLE_SOCIAL";
    }
}
