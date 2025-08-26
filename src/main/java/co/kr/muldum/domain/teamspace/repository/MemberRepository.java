package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByStudentId(Long studentId);
    List<Member> findByTeamId(Long teamId);
    boolean existsByTeamIdAndStudentId(Long teamId, Long studentId);
}
