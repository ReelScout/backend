package click.reelscout.backend.s3;

import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Template s3Template;

    @Value("${secrets.s3.bucket}")
    private String bucketName;

    public String uploadFile(String key, String base64Content) {
        if (base64Content == null || base64Content.trim().isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            s3Template.upload(bucketName, key, new ByteArrayInputStream(decodedBytes));
        } catch (Exception e) {
            throw new S3Exception("Failed to save file", null);
        }

        return key;
    }

    public void deleteFile(String key) {
        try {
            s3Template.deleteObject(bucketName, key);
        } catch (Exception e) {
            throw new S3Exception("Failed to delete file", null);
        }
    }

    public String getFile(String key) {
        if (key == null) {
            return null;
        }

        try {
            byte[] file = s3Template.download(bucketName, key)
                    .getInputStream()
                    .readAllBytes();

            return Base64.getEncoder().encodeToString(file);
        } catch (IOException e) {
            throw new S3Exception("Failed to retrieve file", null);
        }
    }
}