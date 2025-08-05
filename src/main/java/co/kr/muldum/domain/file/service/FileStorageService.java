package co.kr.muldum.domain.file.service;

import co.kr.muldum.domain.file.exception.FileSizeLimitExceededException;
import co.kr.muldum.domain.file.model.File;
import co.kr.muldum.domain.file.model.FileMetadata;
import co.kr.muldum.domain.file.model.FileType;
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
  private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

  public String upload(MultipartFile multipartFile, String type, Long ownerUserId, String ownerUserType) {
    if(multipartFile.getSize() > MAX_FILE_SIZE){
      throw new FileSizeLimitExceededException("업로드 할 수 있는 파일 크기가 초과되었습니다. (20MB)");
    }

    FileType fileType = FileType.fromString(type);
    String uploadDir = filePathConfig.getUploadDir() + fileType.getUploadSubDir();

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
