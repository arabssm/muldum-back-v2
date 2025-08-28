package co.kr.muldum.domain.token.repository;

import co.kr.muldum.domain.token.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface TokenRepository extends JpaRepository<RefreshToken, String> {

}
