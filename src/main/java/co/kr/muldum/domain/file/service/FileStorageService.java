package co.kr.muldum.domain.file.service;

import co.kr.muldum.domain.file.model.File;
import co.kr.muldum.domain.file.model.FileMetadata;
import co.kr.muldum.domain.file.repository.FileRepository;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.global.config.FilePathConfig;
import co.kr.muldum.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    } else if("TEAMSPACE".equalsIgnoreCase(type)) {
      uploadDir = filePathConfig.getUploadDir() + "/teamspace";
    } else {
      uploadDir = filePathConfig.getUploadDir() + "/notice";
    }

    String savedFileName = FileUtil.saveFile(multipartFile, uploadDir);
    String webPath = "/uploads/" + type.toLowerCase() + "/" + savedFileName;

    File file = File.builder()
            .path(webPath)
            .metadata(FileMetadata.of(
                    Objects.requireNonNull(multipartFile.getOriginalFilename()),
                    Objects.requireNonNull(multipartFile.getContentType()),
                    multipartFile.getSize()
            ))
            .ownerUserId(ownerUserId.intValue())
            .ownerUserType(UserType.valueOf(ownerUserType))
            .build();

    fileRepository.save(file);
    return webPath;
  }

}
