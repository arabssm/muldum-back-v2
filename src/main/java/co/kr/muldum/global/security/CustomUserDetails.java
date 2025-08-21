package co.kr.muldum.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

  private final Long userId;
  private final String userType;
  private final Long teamId;

  public CustomUserDetails(Long userId, String userType, Long teamId) {
    this.userId = userId;
    this.userType = userType;
    this.teamId = teamId;
  }

  @Override
  public String getUsername() {
    return userId.toString(); // 굳이 필요 없으면 그냥 ID 리턴
  }

  @Override
  public String getPassword() {
    return null; // JWT 인증 시 password 사용 안 함
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(); // 권한이 없다면 빈 리스트
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
