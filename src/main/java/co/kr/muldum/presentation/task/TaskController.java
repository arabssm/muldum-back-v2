package co.kr.muldum.presentation.task;

import co.kr.muldum.application.task.dto.TaskDto;
import co.kr.muldum.application.task.service.TaskService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/members")
    public ResponseEntity<List<TaskDto.TeamMemberResponse>> getTeamMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskDto.TeamMemberResponse> members = taskService.getTeamMembers(userDetails.getUserId());
        return ResponseEntity.ok(members);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto.TaskResponse>> getTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskDto.TaskResponse> tasks = taskService.getTasks(userDetails.getUserId());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskDto.TaskResponse> createTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TaskDto.CreateRequest request) {
        TaskDto.TaskResponse createdTask = taskService.createTask(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto.TaskResponse> updateTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long taskId,
            @RequestBody TaskDto.UpdateRequest request) {
        TaskDto.TaskResponse updatedTask = taskService.updateTask(userDetails.getUserId(), taskId, request);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long taskId,
            @RequestBody TaskDto.StatusUpdateRequest request) {
        taskService.updateTaskStatus(userDetails.getUserId(), taskId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long taskId) {
        taskService.deleteTask(userDetails.getUserId(), taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<co.kr.muldum.application.task.dto.DashboardDto.DashboardResponse> getDashboardStatistics(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(taskService.getDashboardStatistics(userDetails.getUserId()));
    }
}
