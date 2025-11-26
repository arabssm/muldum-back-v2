package co.kr.muldum.global.config;

import co.kr.muldum.infrastructure.google.GoogleCredentialFileProvider;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
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
public class GoogleCalendarConfig {

    private final GoogleCredentialFileProvider googleCredentialFileProvider;

    @Value("${google.calendar.credentials-path}")
    private final String credentialsPath;

    @Value("${google.calendar.application-name:muldum-calendar}")
    private final String applicationName;

    @Bean
    public Calendar calendarService() throws IOException, GeneralSecurityException {
        File credentialFile = googleCredentialFileProvider.loadCredentials(credentialsPath);
        try (FileInputStream fileInputStream = new FileInputStream(credentialFile)) {
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(fileInputStream)
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    requestInitializer
            ).setApplicationName(applicationName).build();
        }
    }
}
