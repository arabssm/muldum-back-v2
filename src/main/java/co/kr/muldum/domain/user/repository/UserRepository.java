package co.kr.muldum.domain.user.repository;

import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE users SET profile = jsonb_set(COALESCE(profile, '{}'), '{team_id}', '\"0\"') WHERE profile IS NULL OR profile->>'team_id' IS NULL", nativeQuery = true)
    int updateNullTeamIdToZero();

    // 학번 기반으로 유저 조회 (팀스페이스 초대 시 사용 가능)
    Optional<User> findByStudentId(String studentId);
}