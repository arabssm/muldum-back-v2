package co.kr.muldum.domain.user.model;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class UserInfo {
    private Long userId;
    private String name;
    private List<Long> teamIds;
    private Role role;
    private UserType userType;
}
