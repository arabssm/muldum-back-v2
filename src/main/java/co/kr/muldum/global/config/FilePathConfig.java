package co.kr.muldum.global.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FilePathConfig {
  private final String uploadDir = "./uploads";
}
