package co.kr.muldum.domain.user.repository;

import co.kr.muldum.domain.user.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
