package co.kr.muldum.global.config;

import co.kr.muldum.infrastructure.google.GoogleCredentialFileProvider;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class GoogleSheetsConfig {
  private final GoogleCredentialFileProvider googleCredentialFileProvider;

  @Value("${google.sheets.credentials-path}")
  private final String credentialsPath;

  @Bean
  public Sheets sheetsService() throws IOException, GeneralSecurityException {
    File credentialsFile = googleCredentialFileProvider.loadCredentials(credentialsPath);
    try(FileInputStream fileInputStream = new FileInputStream(credentialsFile)) {
      GoogleCredentials credentials = GoogleCredentials
              .fromStream(fileInputStream)
              .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));

      HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

      return new Sheets.Builder(
              GoogleNetHttpTransport.newTrustedTransport(),
              JacksonFactory.getDefaultInstance(),
              requestInitializer
      ).setApplicationName("MyApp").build();
    }
  }
}
