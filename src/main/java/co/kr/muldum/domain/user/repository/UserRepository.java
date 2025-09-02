package co.kr.muldum.domain.user.repository;

import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 기반 조회
    Optional<User> findByEmail(String email);

    // 프로필의 team_id가 NULL인 경우 0으로 초기화
    @Modifying
    @Query(value = "UPDATE users " +
            "SET profile = jsonb_set(COALESCE(profile, '{}'), '{team_id}', '\"0\"') " +
            "WHERE profile IS NULL OR profile->>'team_id' IS NULL",
            nativeQuery = true)
    int updateNullTeamIdToZero();

    // 학년 + 반 + 번호 + 이름 기반 조회 (팀스페이스 초대 시 사용)
    @Query(value = "SELECT * FROM users u " +
            "WHERE u.profile->>'grade' = :grade " +
            "AND u.profile->>'\"class\"' = :classNum " +
            "AND (u.profile->>'number')::int = CAST(:number AS int) " +
            "AND u.name = :name " +
            "LIMIT 1", nativeQuery = true)
    Optional<User> findByGradeClassNumberAndName(
            @Param("grade") String grade,
            @Param("classNum") String classNum,
            @Param("number") String number,
            @Param("name") String name
    );
    // 기본 키 기반 조회 (JpaRepository가 제공하지만 명시적으로 작성 가능)
    Optional<User> findById(Long id);
}