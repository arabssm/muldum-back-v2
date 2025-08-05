package co.kr.muldum.domain.file.model;

import co.kr.muldum.domain.file.exception.InvalidFileTypeException;

public enum FileType {
  NOTICE,
  BANNER,
  TEAMSPACE;
  public String getUploadSubDir() {
    return switch (this) {
      case NOTICE -> "/notice";
      case BANNER -> "/banner";
      case TEAMSPACE -> "/teamspace";
    };
  }

  public static FileType fromString(String type) {
    try {
      return FileType.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidFileTypeException("존재하지 않는 type입니다.");
    }
  }
}
