package co.kr.muldum.application.task.dto;

import co.kr.muldum.domain.task.model.Task;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record DashboardDto() {

    @Builder
    public record DashboardResponse(
            OverallStats overallStats,
            List<MemberStats> memberStats,
            CategoryStats categoryStats,
            List<TaskSummary> overdueTasks,
            List<TaskSummary> upcomingDeadlineTasks,
            List<MonthlyCompletionStats> monthlyCompletions
    ) {}

    @Builder
    public record OverallStats(
            long total,
            long todo,
            long inProgress,
            long done,
            long unassigned
    ) {}

    @Builder
    public record MemberStats(
            long memberId,
            String memberName,
            long total,
            long todo,
            long inProgress,
            long done
    ) {}

    @Builder
    public record CategoryStats(
            long frontend,
            long backend,
            long design,
            long ai
    ) {}

    @Builder
    public record TaskSummary(
            long taskId,
            String title,
            LocalDate deadline,
            TaskDto.AssigneeInfo assignee
    ) {
        public static TaskSummary from(Task task) {
            return TaskSummary.builder()
                    .taskId(task.getId())
                    .title(task.getTitle())
                    .deadline(task.getDeadline())
                    .assignee(TaskDto.AssigneeInfo.from(task.getAssignee()))
                    .build();
        }
    }

    @Builder
    public record MonthlyCompletionStats(
        int month,
        long success
    ) {}
}
