package co.kr.muldum.domain.file.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileMetadata {
  String name;
  String type;
  long size;
}
