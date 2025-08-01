package co.kr.muldum.domain.user.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfo {
    private Long userId;
    private String name;
    private Long teamId;
    private Role role;
    private UserType userType;
}
