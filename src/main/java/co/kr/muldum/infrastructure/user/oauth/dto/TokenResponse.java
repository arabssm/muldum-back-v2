package co.kr.muldum.infrastructure.user.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenResponse {
  public String access_token;
  public String expires_in;
  public String refresh_token;
  public String scope;
  public String token_type;
  public String id_token;
}
