package co.kr.muldum.global.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleCalendarConfig {

    @Value("${google.calendar.credentials-path}")
    private String credentialsPath;

    @Value("${google.calendar.application-name:muldum-calendar}")
    private String applicationName;

    @Bean
    public Calendar calendarService() throws IOException, GeneralSecurityException {
        try (FileInputStream fileInputStream = new FileInputStream(credentialsPath)) {
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
