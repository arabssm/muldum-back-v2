package co.kr.muldum.domain.token.repository;

import co.kr.muldum.domain.token.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> deleteByRefreshToken(String token);
}
