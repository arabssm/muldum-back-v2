package co.kr.muldum.domain.token.model;

import co.kr.muldum.domain.user.model.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name="refresh_token")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
  @Id
  private Long id;
  @Column(name = "refresh_token", nullable = false, unique = true)
  private String refreshToken;
  @Enumerated(EnumType.STRING)
  @Column(name = "user_type", nullable = false)
  private UserType userType;

  public void updateToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
