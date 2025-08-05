package co.kr.muldum.presentation.file;

import co.kr.muldum.domain.file.exception.FileNotAttachedException;
import co.kr.muldum.domain.file.service.FileStorageService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("ara/files")
public class FileController {

  private final FileStorageService fileStorageService;

  @PostMapping("/upload")
  public ResponseEntity<?> uploadFile(
          @RequestPart("files") MultipartFile[] files,
          @RequestParam(name = "type", defaultValue = "NOTICE") String type,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    if(files == null){
      throw new FileNotAttachedException("파일이 첨부되지 않았습니다.");
    }
    List<String> fileUrl = Arrays.stream(files)
            .map(file -> fileStorageService.upload(
                    file, type, customUserDetails.getUserId(), customUserDetails.getUserType())
            ).toList();

    return ResponseEntity.ok(Map.of("fileUrl", fileUrl));
  }
}
