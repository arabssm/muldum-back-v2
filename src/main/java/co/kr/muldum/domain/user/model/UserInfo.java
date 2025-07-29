package co.kr.muldum.domain.user.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfo {
    private Long userId;
    private String name;
    private Long teamId;
    private String role; //학생 또는 교사
    private UserType userType;
}
