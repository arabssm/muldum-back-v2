package co.kr.muldum.application.task.dto;

import co.kr.muldum.domain.task.model.Task;
import co.kr.muldum.domain.task.model.TaskCategory;
import co.kr.muldum.domain.task.model.TaskStatus;
import co.kr.muldum.domain.user.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TaskDto {

    public record CreateRequest(
            Long assigneeId,
            String title,
            TaskCategory category,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate deadline
    ) {}

    public record UpdateRequest(
            Long assigneeId,
            String title,
            TaskStatus status,
            TaskCategory category,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate deadline
    ) {}

    public record StatusUpdateRequest(
            TaskStatus status
    ) {}

    public record TaskResponse(
            Long id,
            AssigneeInfo assignee,
            String title,
            TaskStatus status,
            TaskCategory category,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate deadline,
            LocalDateTime createdAt
    ) {
        public static TaskResponse from(Task task) {
            return new TaskResponse(
                    task.getId(),
                    AssigneeInfo.from(task.getAssignee()),
                    task.getTitle(),
                    task.getStatus(),
                    task.getCategory(),
                    task.getDeadline(),
                    task.getCreatedAt()
            );
        }
    }

    public record AssigneeInfo(
            Long id,
            String name
    ) {
        public static AssigneeInfo from(User user) {
            if (user == null) {
                return null;
            }
            return new AssigneeInfo(user.getId(), user.getName());
        }
    }

    public record TeamMemberResponse(
            Long id,
            String name,
            String email
    ) {
        public static TeamMemberResponse from(User user) {
            return new TeamMemberResponse(user.getId(), user.getName(), user.getEmail());
        }
    }
}
