package co.kr.muldum.application.task.service;

import co.kr.muldum.application.report.support.UserTeamResolver;
import co.kr.muldum.application.task.dto.DashboardDto;
import co.kr.muldum.application.task.dto.TaskDto;
import co.kr.muldum.calendar.application.StudentCalendarService;
import co.kr.muldum.calendar.application.dto.StudentCalendarResponse;
import co.kr.muldum.calendar.domain.StudentCalendar;
import co.kr.muldum.calendar.domain.StudentCalendarRepository;
import co.kr.muldum.calendar.presentation.dto.StudentCalendarRequest;
import co.kr.muldum.domain.task.model.Task;
import co.kr.muldum.domain.task.model.TaskCategory;
import co.kr.muldum.domain.task.model.TaskStatus;
import co.kr.muldum.domain.task.repository.TaskRepository;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserTeamResolver userTeamResolver;
    private final StudentCalendarRepository studentCalendarRepository;
    private final StudentCalendarService studentCalendarService;

    public DashboardDto.DashboardResponse getDashboardStatistics(Long userId) {
        Long teamId = userTeamResolver.resolveTeamId(userId);
        Team team = findTeamById(teamId);
        List<User> members = userRepository.findByTeamId(teamId);
        List<Task> tasks = taskRepository.findByTeamWithAssignee(team);

        // 1. Overall Stats
        DashboardDto.OverallStats overallStats = DashboardDto.OverallStats.builder()
                .total(tasks.size())
                .todo(tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count())
                .inProgress(tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count())
                .done(tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count())
                .unassigned(tasks.stream().filter(t -> t.getAssignee() == null).count())
                .build();

        // 2. Member Stats
        Map<Long, User> memberMap = members.stream().collect(Collectors.toMap(User::getId, user -> user));
        Map<Long, List<Task>> tasksByMember = tasks.stream()
                .filter(t -> t.getAssignee() != null)
                .collect(Collectors.groupingBy(t -> t.getAssignee().getId()));

        List<DashboardDto.MemberStats> memberStats = new ArrayList<>();
        for (User member : members) {
            List<Task> memberTasks = tasksByMember.getOrDefault(member.getId(), List.of());
            memberStats.add(DashboardDto.MemberStats.builder()
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .total(memberTasks.size())
                    .todo(memberTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count())
                    .inProgress(memberTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count())
                    .done(memberTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count())
                    .build());
        }

        // 3. Category Stats
        DashboardDto.CategoryStats categoryStats = DashboardDto.CategoryStats.builder()
                .frontend(tasks.stream().filter(t -> t.getCategory() == TaskCategory.FRONTEND).count())
                .backend(tasks.stream().filter(t -> t.getCategory() == TaskCategory.BACKEND).count())
                .design(tasks.stream().filter(t -> t.getCategory() == TaskCategory.DESIGN).count())
                .ai(tasks.stream().filter(t -> t.getCategory() == TaskCategory.AI).count())
                .build();

        // 4. Overdue & Upcoming Tasks
        LocalDate now = LocalDate.now();
        List<DashboardDto.TaskSummary> overdueTasks = tasks.stream()
                .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(now) && t.getStatus() != TaskStatus.DONE)
                .map(DashboardDto.TaskSummary::from)
                .collect(Collectors.toList());

        List<DashboardDto.TaskSummary> upcomingDeadlineTasks = tasks.stream()
                .filter(t -> t.getDeadline() != null && !t.getDeadline().isBefore(now) && t.getDeadline().isBefore(now.plusDays(8)))
                .map(DashboardDto.TaskSummary::from)
                .collect(Collectors.toList());

        // 5. Monthly Completion Stats
        List<Task> doneTasks = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE && t.getUpdatedAt().getYear() == now.getYear())
                .collect(Collectors.toList());

        Map<Integer, List<Task>> doneTasksByMonth = doneTasks.stream()
                .collect(Collectors.groupingBy(t -> t.getUpdatedAt().getMonthValue()));

        List<DashboardDto.MonthlyCompletionStats> monthlyCompletions = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            List<Task> monthTasks = doneTasksByMonth.getOrDefault(i, List.of());
            monthlyCompletions.add(DashboardDto.MonthlyCompletionStats.builder()
                    .month(i)
                    .success(monthTasks.size())
                    .build());
        }

        return DashboardDto.DashboardResponse.builder()
                .overallStats(overallStats)
                .memberStats(memberStats)
                .categoryStats(categoryStats)
                .overdueTasks(overdueTasks)
                .upcomingDeadlineTasks(upcomingDeadlineTasks)
                .monthlyCompletions(monthlyCompletions)
                .build();
    }

    public List<TaskDto.TeamMemberResponse> getTeamMembers(Long userId) {
        Long teamId = userTeamResolver.resolveTeamId(userId);
        List<User> teamMembers = userRepository.findByTeamId(teamId);
        return teamMembers.stream()
                .map(TaskDto.TeamMemberResponse::from)
                .collect(Collectors.toList());
    }

    public List<TaskDto.TaskResponse> getTasks(Long userId) {
        Long teamId = userTeamResolver.resolveTeamId(userId);
        Team team = findTeamById(teamId);
        List<Task> tasks = taskRepository.findByTeamWithAssignee(team);
        return tasks.stream()
                .map(TaskDto.TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDto.TaskResponse createTask(Long userId, TaskDto.CreateRequest request) {
        Long teamId = userTeamResolver.resolveTeamId(userId);
        Team team = findTeamById(teamId);
        User assignee = findUserById(request.assigneeId());

        if (assignee != null) {
            Long assigneeTeamId = userTeamResolver.resolveTeamId(assignee.getId());
            if (!Objects.equals(teamId, assigneeTeamId)) {
                throw new CustomException(ErrorCode.USER_NOT_IN_SAME_TEAM);
            }
        }

        Task task = Task.builder()
                .team(team)
                .assignee(assignee)
                .title(request.title())
                .status(co.kr.muldum.domain.task.model.TaskStatus.TODO)
                .category(request.category())
                .deadline(request.deadline())
                .build();
        taskRepository.save(task);

        if (task.getDeadline() != null) {
            createCalendarEntry(userId, team, task);
        }

        return TaskDto.TaskResponse.from(task);
    }

    @Transactional
    public TaskDto.TaskResponse updateTask(Long userId, Long taskId, TaskDto.UpdateRequest request) {
        Long teamId = userTeamResolver.resolveTeamId(userId);
        Task task = findTaskById(taskId);
        validateTeamAccess(teamId, task.getTeam().getId());

        User assignee = findUserById(request.assigneeId());
        if (assignee != null) {
            Long assigneeTeamId = userTeamResolver.resolveTeamId(assignee.getId());
            if (!Objects.equals(teamId, assigneeTeamId)) {
                throw new CustomException(ErrorCode.USER_NOT_IN_SAME_TEAM);
            }
        }

        LocalDate oldDeadline = task.getDeadline();
        String oldTitle = task.getTitle();
        task.update(assignee, request.title(), request.status(), request.category(), request.deadline());

        handleCalendarUpdate(userId, task, oldDeadline, oldTitle);

        return TaskDto.TaskResponse.from(task);
    }

    @Transactional
    public void updateTaskStatus(Long userId, Long taskId, TaskDto.StatusUpdateRequest request) {
        Long teamId = userTeamResolver.resolveTeamId(userId);
        Task task = findTaskById(taskId);
        validateTeamAccess(teamId, task.getTeam().getId());

        task.updateStatus(request.status());
    }

    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        Long teamId = userTeamResolver.resolveTeamId(userId);
        Task task = findTaskById(taskId);
        validateTeamAccess(teamId, task.getTeam().getId());

        studentCalendarRepository.findByTaskId(taskId).ifPresent(calendarEntry -> {
            try {
                studentCalendarService.delete(userId, calendarEntry.getId());
            } catch (Exception e) {
                log.error("Failed to delete calendar event for task ID: {}", taskId, e);
            }
        });

        taskRepository.delete(task);
    }

    private void handleCalendarUpdate(Long userId, Task task, LocalDate oldDeadline, String oldTitle) {
        Optional<StudentCalendar> existingCalendar = studentCalendarRepository.findByTaskId(task.getId());
        LocalDate newDeadline = task.getDeadline();
        String newTitle = task.getTitle();

        boolean deadlineChanged = !Objects.equals(oldDeadline, newDeadline);
        boolean titleChanged = !Objects.equals(oldTitle, newTitle);

        if (existingCalendar.isPresent()) {
            StudentCalendar calendar = existingCalendar.get();
            if (newDeadline == null) {
                // Deadline removed
                studentCalendarService.delete(userId, calendar.getId());
            } else if (deadlineChanged || titleChanged) {
                // Deadline or title updated
                StudentCalendarRequest updateRequest = new StudentCalendarRequest();
                updateRequest.setStartYear(newDeadline.getYear());
                updateRequest.setStartDate(newDeadline.getMonthValue() * 100 + newDeadline.getDayOfMonth());
                updateRequest.setEndYear(newDeadline.getYear());
                updateRequest.setEndDate(newDeadline.getMonthValue() * 100 + newDeadline.getDayOfMonth());
                updateRequest.setTitle("전공동아리");
                updateRequest.setContent(newTitle);
                studentCalendarService.update(userId, calendar.getId(), updateRequest);
            }
        } else if (newDeadline != null) {
            // Deadline added
            createCalendarEntry(userId, task.getTeam(), task);
        }
    }

    private void createCalendarEntry(Long userId, Team team, Task task) {
        try {
            StudentCalendarRequest request = new StudentCalendarRequest();
            request.setStartYear(task.getDeadline().getYear());
            request.setStartDate(task.getDeadline().getMonthValue() * 100 + task.getDeadline().getDayOfMonth());
            request.setEndYear(task.getDeadline().getYear());
            request.setEndDate(task.getDeadline().getMonthValue() * 100 + task.getDeadline().getDayOfMonth());
            request.setTitle("전공동아리");
            request.setContent(task.getTitle());

            StudentCalendarResponse calendarResponse = studentCalendarService.create(userId, request);
            studentCalendarRepository.findById(calendarResponse.getCalendarId()).ifPresent(cal -> {
                cal.setTaskId(task.getId());
                studentCalendarRepository.save(cal);
            });
        } catch (Exception e) {
            log.error("Failed to create calendar event for task ID: {}", task.getId(), e);
        }
    }

    private User findUserById(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    private Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_TASK));
    }

    private void validateTeamAccess(Long userTeamId, Long resourceTeamId) {
        if (!Objects.equals(userTeamId, resourceTeamId)) {
            throw new CustomException(ErrorCode.TEAM_ACCESS_DENIED);
        }
    }
}
