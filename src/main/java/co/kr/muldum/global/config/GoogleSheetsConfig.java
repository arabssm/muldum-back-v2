package co.kr.muldum.global.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.api.services.sheets.v4.Sheets;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleSheetsConfig {
  @Value("${google.sheets.credentials-path}")
  private String credentialsPath;

  @Bean
  public Sheets sheetsService() throws IOException, GeneralSecurityException {
    GoogleCredentials credentials = GoogleCredentials
            .fromStream(new FileInputStream(credentialsPath))
            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));

    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

    return new Sheets.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            requestInitializer
    ).setApplicationName("MyApp").build();
  }
}
