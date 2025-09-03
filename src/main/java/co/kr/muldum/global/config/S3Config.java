package co.kr.muldum.global.config;

import co.kr.muldum.global.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {
  private final S3Properties s3Properties;

  @Bean
  @Primary
  public AwsCredentials awsCredentials() {
    return AwsBasicCredentials.create(
            s3Properties.getAccessKey(),
            s3Properties.getSecretKey()
    );
  }

  @Bean
  public AwsCredentialsProvider credentialsProvider() {
    return this::awsCredentials;
  }

  @Bean
  public S3Client s3Client(
          AwsCredentialsProvider awsCredentialsProvider
  ) {
    return S3Client.builder()
            .region(Region.of(s3Properties.getRegion()))
            .credentialsProvider(awsCredentialsProvider)
            .build();
  }
}
