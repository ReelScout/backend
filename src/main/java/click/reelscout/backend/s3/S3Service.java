package click.reelscout.backend.s3;

import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Service for handling file operations with Amazon S3.
 */
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Template s3Template;

    @Value("${secrets.s3.bucket}")
    private String bucketName;

    /**
     * Uploads a file to S3.
     *
     * @param key            the key (path) where the file will be stored in S3
     * @param base64Content  the file content encoded in Base64
     * @return the key of the uploaded file, or null if the content is empty
     */
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

    /**
     * Deletes a file from S3.
     *
     * @param key the key (path) of the file to be deleted in S3
     */
    public void deleteFile(String key) {
        try {
            s3Template.deleteObject(bucketName, key);
        } catch (Exception e) {
            throw new S3Exception("Failed to delete file", null);
        }
    }

    /**
     * Retrieves a file from S3 and returns its content encoded in Base64.
     *
     * @param key the key (path) of the file to be retrieved from S3
     * @return the file content encoded in Base64, or null if the key is null
     */
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