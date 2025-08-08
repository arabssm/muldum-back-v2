package co.kr.muldum.infrastructure.user.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfoDto {
    private String email;
    private String name;
    private String picture;
}
