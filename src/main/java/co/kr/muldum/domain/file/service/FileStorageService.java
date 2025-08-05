package co.kr.muldum.domain.file.service;

import co.kr.muldum.domain.file.model.File;
import co.kr.muldum.domain.file.repository.FileRepository;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.global.config.FilePathConfig;
import co.kr.muldum.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
  private final FileRepository fileRepository;
  private final FilePathConfig filePathConfig;

  public String upload(MultipartFile multipartFile, String type, Long ownerUserId, String ownerUserType) {
    String uploadDir;

    if ("BANNER".equalsIgnoreCase(type)) {
      uploadDir = filePathConfig.getUploadDir() + "/banner";
    } else {
      uploadDir = filePathConfig.getUploadDir() + "/notice";
    }

    String savedFileName = FileUtil.saveFile(multipartFile, uploadDir);
    String webPath = "/uploads/" + type.toLowerCase() + "/" + savedFileName;

    File file = File.builder()
            .path(webPath)
            .metadata(Map.of(
                    "name", Objects.requireNonNull(multipartFile.getOriginalFilename()),
                    "type", Objects.requireNonNull(multipartFile.getContentType()),
                    "size_bytes", multipartFile.getSize()
            ))
            .ownerUserId(ownerUserId.intValue()) // 임시 사용자 ID
            .ownerUserType(UserType.valueOf(ownerUserType)) // 임시 사용자 타입
            .build();

    fileRepository.save(file);
    return webPath;
  }

}
