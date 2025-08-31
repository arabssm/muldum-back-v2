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
  private String refreshToken;
  private Long id;
  @Enumerated(EnumType.STRING)
  private UserType userType;

}
