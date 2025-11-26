package co.kr.muldum.domain.task.repository;

import co.kr.muldum.domain.task.model.Task;
import co.kr.muldum.domain.teamspace.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTeam(Team team);

    @Query("SELECT t FROM Task t JOIN FETCH t.assignee WHERE t.team = :team ORDER BY t.createdAt DESC")
    List<Task> findByTeamWithAssignee(@Param("team") Team team);
}
