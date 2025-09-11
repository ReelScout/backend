package click.reelscout.backend.s3;

import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link S3Service}.
 * Covers upload, delete, and get file functionalities including error handling.
 */
class S3ServiceTest {

    @Mock
    private S3Template s3Template;

    @InjectMocks
    private S3Service s3Service;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            var bucketField = S3Service.class.getDeclaredField("bucketName");
            bucketField.setAccessible(true);
            bucketField.set(s3Service, bucketName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests for uploadFile method
     */
    @Test
    void uploadFile_returnsKey_whenValidBase64() {
        String key = "file.txt";
        String base64 = Base64.getEncoder().encodeToString("hello".getBytes());

        String result = s3Service.uploadFile(key, base64);

        assertEquals(key, result);
        verify(s3Template).upload(eq(bucketName), eq(key), any(ByteArrayInputStream.class));
    }

    /**
     * Tests for uploadFile method with invalid base64 input
     */
    @Test
    void uploadFile_returnsNull_whenEmptyContent() {
        String key = "file.txt";

        assertNull(s3Service.uploadFile(key, ""));
        verifyNoInteractions(s3Template);
    }

    /**
     * Tests for uploadFile method throwing S3Exception on error
     */
    @Test
    void uploadFile_throwsS3Exception_onError() {
        String key = "file.txt";
        String base64 = Base64.getEncoder().encodeToString("data".getBytes());

        doThrow(new RuntimeException("boom"))
                .when(s3Template).upload(eq(bucketName), eq(key), any(ByteArrayInputStream.class));

        assertThrows(S3Exception.class, () -> s3Service.uploadFile(key, base64));
    }

    /**
     * Tests for deleteFile method
     */
    @Test
    void deleteFile_callsS3Template() {
        String key = "file.txt";

        s3Service.deleteFile(key);

        verify(s3Template).deleteObject(bucketName, key);
    }

    /**
     * Tests for deleteFile method throwing S3Exception on error
     */
    @Test
    void deleteFile_throwsS3Exception_onError() {
        String key = "file.txt";

        doThrow(new RuntimeException("fail"))
                .when(s3Template).deleteObject(bucketName, key);

        assertThrows(S3Exception.class, () -> s3Service.deleteFile(key));
    }

    /**
     * Tests for getFile method
     */
    @Test
    void getFile_returnsBase64Content() throws IOException {
        String key = "file.txt";
        byte[] data = "hello".getBytes();
        String expectedBase64 = Base64.getEncoder().encodeToString(data);

        S3Resource s3Resource = mock(S3Resource.class);
        when(s3Resource.getInputStream()).thenReturn(new ByteArrayInputStream(data));
        when(s3Template.download(bucketName, key)).thenReturn(s3Resource);

        String result = s3Service.getFile(key);

        assertEquals(expectedBase64, result);
        verify(s3Template).download(bucketName, key);
    }

    /**
     * Tests for getFile method with null key
     */
    @Test
    void getFile_returnsNull_whenKeyIsNull() {
        assertNull(s3Service.getFile(null));
        verifyNoInteractions(s3Template);
    }

    /**
     * Tests for getFile method throwing S3Exception on IOException
     */
    @Test
    void getFile_throwsS3Exception_onIOException() throws IOException {
        String key = "file.txt";

        S3Resource s3Resource = mock(S3Resource.class);
        when(s3Resource.getInputStream()).thenThrow(new IOException("fail"));
        when(s3Template.download(bucketName, key)).thenReturn(s3Resource);

        assertThrows(S3Exception.class, () -> s3Service.getFile(key));
    }
}