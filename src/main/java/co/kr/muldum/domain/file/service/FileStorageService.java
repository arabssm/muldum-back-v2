package co.kr.muldum.domain.file.service;

import co.kr.muldum.domain.file.model.File;
import co.kr.muldum.domain.file.model.FileMetadata;
import co.kr.muldum.domain.file.repository.FileRepository;
import co.kr.muldum.global.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    private final FileRepository fileRepository;
    private final S3Properties s3Properties;
    private final AwsCredentialsProvider awsCredentialsProvider;

    public String generatePreSignedUrlToUpload(String fileName, Long userId) {
        String encodedFileName = UUID.randomUUID() + "_" + fileName;
        String objectName = userId + "/" + encodedFileName;

        try (S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.AP_NORTHEAST_2)
                .build()) {

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(objectName)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(putRequest)
                    .build();

            // Presigned URL 발급
            String presignedUrl = presigner.presignPutObject(presignRequest)
                    .url()
                    .toExternalForm();

            File file = File.create(
                    objectName,
                    FileMetadata.of(fileName, null, 0L),
                    userId
            );
            fileRepository.save(file);

            return presignedUrl;

        }
    }
}
