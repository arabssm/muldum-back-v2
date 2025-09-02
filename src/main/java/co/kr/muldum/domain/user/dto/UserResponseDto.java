package co.kr.muldum.domain.user.dto;

import co.kr.muldum.domain.user.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String profile;
    private String user_type;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profile(user.getProfile().toString())
                .user_type(user.getUserType().toString())
                .build();
    }
}
