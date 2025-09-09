package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.FileUploadService;
import co.kr.muldum.application.teamspace.dto.TeamFileRequest;
import co.kr.muldum.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("std/teamspace")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class FileUploadController {

  private final FileUploadService fileUploadService;

  @PatchMapping("/network/team/{team-id}/banner")
  public ResponseEntity<String> uploadTeamNetwork(
          @PathVariable("team-id") Long teamId,
          @Valid @RequestBody TeamFileRequest teamFileRequest,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    fileUploadService.uploadTeamBanner(teamId, teamFileRequest, customUserDetails.getUserId());
    return ResponseEntity.ok("파일 업로드 성공");
  }

  @PatchMapping("/network/team/{team-id}/icon")
  public ResponseEntity<String> uploadTeamIcon(
          @PathVariable("team-id") Long teamId,
          @Valid @RequestBody TeamFileRequest teamFileRequest,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    fileUploadService.uploadTeamIcon(teamId, teamFileRequest, customUserDetails.getUserId());
    return ResponseEntity.ok("아이콘이 성공적으로 수정됐습니다.");
  }
}
