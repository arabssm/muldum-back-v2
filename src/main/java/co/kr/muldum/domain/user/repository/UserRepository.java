package co.kr.muldum.domain.user.repository;

import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // studentNumber로 유저 조회
    Optional<User> findByStudentNumber(String studentNumber);
}