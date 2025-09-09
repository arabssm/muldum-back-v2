package co.kr.muldum.presentation.file;

import co.kr.muldum.domain.file.exception.FileNotAttachedException;
import co.kr.muldum.domain.file.service.FileStorageService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("files")
public class FileController {

  private final FileStorageService fileStorageService;

  @GetMapping("/presigned")
  public ResponseEntity<String> getPresignedUrl(
          @RequestParam("fileName") String fileName,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    if(fileName == null){
      throw new FileNotAttachedException("파일이 첨부되지 않았습니다.");
    }
    String url = fileStorageService.generatePreSignedUrlToUpload(fileName, customUserDetails.getUserId());
    return ResponseEntity.ok(url);
  }
}
