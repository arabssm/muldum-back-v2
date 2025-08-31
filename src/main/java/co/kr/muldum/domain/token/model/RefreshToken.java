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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String refreshToken;
  @Enumerated(EnumType.STRING)
  @Column(name = "user_type")
  private UserType userType;

}
