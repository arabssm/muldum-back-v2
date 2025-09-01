package co.kr.muldum.domain.user.repository;

import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 기반으로 유저 조회
    Optional<User> findByEmail(String email);

    // 학번 기반으로 유저 조회 (팀스페이스 초대 시 사용 가능)
    Optional<User> findByStudentId(String studentId);
}