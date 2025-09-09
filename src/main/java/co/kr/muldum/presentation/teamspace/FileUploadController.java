package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.FileUploadService;
import co.kr.muldum.application.teamspace.dto.TeamBannerRequest;
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
          @Valid @RequestBody TeamBannerRequest teamBannerRequest,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    fileUploadService.uploadTeamFile(teamId, teamBannerRequest, customUserDetails.getUserId());
    return ResponseEntity.ok("파일 업로드 성공");
  }
}
