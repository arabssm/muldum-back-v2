package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.FileUploadService;
import co.kr.muldum.application.teamspace.dto.TeamBannerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("std/teamspace")
@RequiredArgsConstructor
public class FileUploadController {

  private final FileUploadService fileUploadService;

  @PatchMapping("/network/team/{teamId}")
  public ResponseEntity<String> uploadTeamNetwork(
          @PathVariable Long teamId,
          @RequestBody TeamBannerRequest teamBannerRequest,
          @RequestParam(name = "type", defaultValue = "BANNER") String type
  ) {
    fileUploadService.uploadTeamFile(teamId, teamBannerRequest, type);
    return ResponseEntity.ok("파일 업로드 성공");
  }
}
