package co.kr.muldum.infrastructure.user.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleUserInfoDto {
    private String email;
    private Boolean email_verified;
    private String hd;
}
