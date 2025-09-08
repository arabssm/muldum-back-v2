package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.FileUploadService;
import co.kr.muldum.application.teamspace.dto.TeamBannerRequest;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("std/teamspace")
@RequiredArgsConstructor
public class FileUploadController {

  private final FileUploadService fileUploadService;

  @PatchMapping("/network/team/{teamId}/banner")
  public ResponseEntity<String> uploadTeamNetwork(
          @PathVariable Long teamId,
          @RequestBody TeamBannerRequest teamBannerRequest,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    fileUploadService.uploadTeamFile(teamId, teamBannerRequest, customUserDetails.getUserId());
    return ResponseEntity.ok("파일 업로드 성공");
  }
}
