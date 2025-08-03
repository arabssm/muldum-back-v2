package co.kr.muldum.presentation.file;

import co.kr.muldum.domain.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
          @RequestParam(name = "type", defaultValue = "NOTICE") String type
          // TODO: 사용자 정보 추가 (예: @AuthenticationPrincipal UserDetails userDetails)
  ) {
    List<String> fileUrl = Arrays.stream(files)
            .map(file -> fileStorageService.upload(file, type))
            .toList();

    return ResponseEntity.ok(Map.of("fileUrl", fileUrl));
  }
}
