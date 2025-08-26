package co.kr.muldum.domain.token.model;

import co.kr.muldum.domain.user.model.UserType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
  private UserType userType;



}
