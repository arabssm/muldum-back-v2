package co.kr.muldum.domain.user.repository;

import co.kr.muldum.domain.user.model.UserNotionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserNotionTokenRepository extends JpaRepository<UserNotionToken, Long> {
    Optional<UserNotionToken> findByUserId(Long userId);
}
