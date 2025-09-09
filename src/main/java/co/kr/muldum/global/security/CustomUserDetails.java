package co.kr.muldum.global.security;

import co.kr.muldum.domain.user.model.Role;
import co.kr.muldum.domain.user.model.UserType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class CustomUserDetails implements UserDetails {

  private final Long userId;
  private final String userType;

  public CustomUserDetails(Long userId, String userType) {
    this.userId = userId;
    this.userType = userType;
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
    if (UserType.SUPER.name().equals(userType)) {
      Stream<GrantedAuthority> userTypeRoles = Arrays.stream(UserType.values())
          .map(type -> new SimpleGrantedAuthority("ROLE_" + type.name()));
      Stream<GrantedAuthority> roleEnumRoles = Arrays.stream(Role.values())
          .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()));
      return Stream.concat(userTypeRoles, roleEnumRoles).collect(Collectors.toList());
    }
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType));
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
