package org.example.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import org.example.common.exception.BusinessException;
import org.example.config.MinioProperties;
import org.example.vo.FeedbackAttachmentUploadVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Service
public class MinioStorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);
    private static final String IMAGE_WEBP = "image/webp";

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            IMAGE_WEBP
    );

    private static final Set<String> ALLOWED_ATTACHMENT_TYPES = Set.of(
            MediaType.APPLICATION_PDF_VALUE,
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "text/csv",
            MediaType.APPLICATION_OCTET_STREAM_VALUE
    );

    private static final Set<String> ALLOWED_ATTACHMENT_EXTENSIONS = Set.of(
            ".pdf", ".xls", ".xlsx", ".doc", ".docx", ".txt", ".csv"
    );

    private final MinioProperties properties;

    public MinioStorageService(MinioProperties properties) {
        this.properties = properties;
    }

    public FeedbackAttachmentUploadVO uploadFeedbackAttachment(MultipartFile file, Long userId) {
        ensureConfigured();
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Please select a feedback attachment");
        }

        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        String fileType = resolveFileType(contentType, extension);
        if (!"IMAGE".equals(fileType)
                && (!ALLOWED_ATTACHMENT_TYPES.contains(contentType) || !ALLOWED_ATTACHMENT_EXTENSIONS.contains(extension))) {
            throw new BusinessException("Only images, PDF, Excel, Word, TXT and CSV files are supported");
        }

        long maxBytes = properties.getMaxFileSizeMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BusinessException("Attachment size cannot exceed " + properties.getMaxFileSizeMb() + " MB");
        }

        String objectKey = buildObjectKey(userId, extension);
        uploadObject(file, contentType, objectKey);
        String fileName = sanitizeFileName(file.getOriginalFilename());
        return new FeedbackAttachmentUploadVO(
                objectKey,
                fileName,
                contentType,
                file.getSize(),
                fileType,
                buildFeedbackAttachmentUrl(objectKey, fileName)
        );
    }

    public InputStream getObjectStream(String objectKey) {
        ensureConfigured();
        if (!StringUtils.hasText(objectKey)) {
            throw new BusinessException("Attachment object key cannot be empty");
        }

        try {
            return buildClient().getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .build()
            );
        } catch (ErrorResponseException ex) {
            if ("NoSuchKey".equals(ex.errorResponse().code())) {
                throw new BusinessException("Attachment does not exist");
            }
            throw new BusinessException("Failed to read feedback attachment: " + ex.getMessage());
        } catch (Exception ex) {
            throw new BusinessException("Failed to read feedback attachment: " + ex.getMessage());
        }
    }

    public StatObjectResponse statObject(String objectKey) {
        ensureConfigured();
        if (!StringUtils.hasText(objectKey)) {
            throw new BusinessException("Attachment object key cannot be empty");
        }

        try {
            return buildClient().statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .build()
            );
        } catch (ErrorResponseException ex) {
            if ("NoSuchKey".equals(ex.errorResponse().code())) {
                throw new BusinessException("Attachment does not exist");
            }
            throw new BusinessException("Failed to read attachment metadata: " + ex.getMessage());
        } catch (Exception ex) {
            throw new BusinessException("Failed to read attachment metadata: " + ex.getMessage());
        }
    }

    public String buildFeedbackAttachmentUrl(String objectKey, String fileName) {
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        String encodedKey = URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
        String url = properties.getPublicBaseUrl() + "/feedback/attachments?objectKey=" + encodedKey;
        if (StringUtils.hasText(fileName)) {
            url += "&fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        }
        return url;
    }

    public void deleteObjectQuietly(String objectKey) {
        if (!properties.isConfigured() || !StringUtils.hasText(objectKey)) {
            return;
        }
        try {
            buildClient().removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .build()
            );
            log.info("deleted feedback minio object objectKey={}", objectKey);
        } catch (Exception ex) {
            log.warn("failed to delete feedback minio object objectKey={} message={}",
                    objectKey, ex.getMessage());
        }
    }

    private void uploadObject(MultipartFile file, String contentType, String objectKey) {
        MinioClient client = buildClient();

        try (InputStream inputStream = file.getInputStream()) {
            ensureBucketExists(client);
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
            log.info("uploaded feedback attachment objectKey={} size={} contentType={}",
                    objectKey, file.getSize(), contentType);
        } catch (Exception ex) {
            throw new BusinessException("Failed to upload feedback attachment: " + ex.getMessage());
        }
    }

    private void ensureConfigured() {
        if (!properties.isConfigured()) {
            throw new BusinessException("MinIO is not fully configured");
        }
    }

    private MinioClient buildClient() {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

    private void ensureBucketExists(MinioClient client) throws Exception {
        boolean exists = client.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(properties.getBucket())
                        .build()
        );
        if (!exists) {
            client.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(properties.getBucket())
                            .build()
            );
        }
    }

    private String buildObjectKey(Long userId, String extension) {
        String safeUserId = userId == null ? "anonymous" : String.valueOf(userId);
        return "feedback/tmp/" + safeUserId + "/" + UUID.randomUUID().toString().replace("-", "") + extension;
    }

    private String resolveFileType(String contentType, String extension) {
        if (ALLOWED_IMAGE_TYPES.contains(contentType) || Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp").contains(extension)) {
            return "IMAGE";
        }
        return "FILE";
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
            if (ext.length() <= 10) {
                return ext;
            }
        }
        return switch (contentType) {
            case MediaType.IMAGE_JPEG_VALUE -> ".jpg";
            case MediaType.IMAGE_PNG_VALUE -> ".png";
            case MediaType.IMAGE_GIF_VALUE -> ".gif";
            case IMAGE_WEBP -> ".webp";
            case MediaType.APPLICATION_PDF_VALUE -> ".pdf";
            case "application/vnd.ms-excel" -> ".xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> ".xlsx";
            case "application/msword" -> ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
            case "text/plain" -> ".txt";
            case "text/csv" -> ".csv";
            default -> ".bin";
        };
    }

    private String sanitizeFileName(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "attachment";
        }
        String fileName = originalFilename.replace("\\", "/");
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        fileName = fileName.replaceAll("[\\r\\n\\t]", "_").trim();
        return StringUtils.hasText(fileName) ? fileName : "attachment";
    }
}
