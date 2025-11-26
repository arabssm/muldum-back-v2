package co.kr.muldum.infrastructure.google;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GoogleCredentialFileProvider {

    private final S3Client s3Client;

    public File loadCredentials(String path) throws IOException {
        if (path == null || path.isBlank()) {
            throw new IOException("Google credentials path is not configured");
        }

        if (path.startsWith("s3://")) {
            return downloadFromS3(path);
        }

        return new File(path);
    }

    private File downloadFromS3(String s3Path) throws IOException {
        String noPrefix = s3Path.replaceFirst("^s3://", "");
        int separatorIndex = noPrefix.indexOf("/");
        if (separatorIndex < 0) {
            throw new IOException("Invalid S3 path for Google credentials: " + s3Path);
        }

        String bucket = noPrefix.substring(0, separatorIndex);
        String key = noPrefix.substring(separatorIndex + 1);

        File temp = File.createTempFile("google-key-", ".json");
        s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                ResponseTransformer.toFile(temp)
        );
        return temp;
    }
}
