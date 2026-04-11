package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.common.result.Result;
import org.example.dto.FeedbackCreateRequest;
import org.example.dto.FeedbackMessageRequest;
import org.example.dto.FeedbackStatusUpdateRequest;
import org.example.entity.FeedbackMessageAttachment;
import org.example.service.FeedbackService;
import org.example.service.MinioStorageService;
import org.example.vo.FeedbackAttachmentUploadVO;
import org.example.vo.FeedbackDetailVO;
import org.example.vo.FeedbackVO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final MinioStorageService minioStorageService;

    public FeedbackController(FeedbackService feedbackService,
                              MinioStorageService minioStorageService) {
        this.feedbackService = feedbackService;
        this.minioStorageService = minioStorageService;
    }

    @PostMapping
    public Result<FeedbackDetailVO> createFeedback(
            @RequestBody FeedbackCreateRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return Result.success(feedbackService.createFeedback(request, userId));
    }

    @GetMapping("/my")
    public Result<IPage<FeedbackVO>> listMyFeedback(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestHeader("X-User-Id") Long userId) {
        return Result.success(feedbackService.listMyFeedback(page, size, status, userId));
    }

    @GetMapping("/admin/list")
    public Result<IPage<FeedbackVO>> listAdminFeedback(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String keyword,
            @RequestHeader("X-User-Role") String role) {
        if (!FeedbackService.ROLE_ADMIN.equals(role)) {
            return Result.forbidden("Only administrators can view feedback management data");
        }
        return Result.success(feedbackService.listAdminFeedback(page, size, status, category, priority, keyword));
    }

    @GetMapping("/admin/{id}")
    public Result<FeedbackDetailVO> getAdminFeedbackDetail(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        if (!FeedbackService.ROLE_ADMIN.equals(role)) {
            return Result.forbidden("Only administrators can view feedback management data");
        }
        return Result.success(feedbackService.getFeedbackDetail(id, userId, role));
    }

    @PostMapping("/admin/{id}/messages")
    public Result<FeedbackDetailVO> replyFeedbackAsAdmin(
            @PathVariable Long id,
            @RequestBody FeedbackMessageRequest request,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        if (!FeedbackService.ROLE_ADMIN.equals(role)) {
            return Result.forbidden("Only administrators can reply feedback");
        }
        return Result.success(feedbackService.replyAsAdmin(id, request, userId));
    }

    @PostMapping("/admin/{id}/close")
    public Result<Void> closeFeedbackAsAdmin(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        if (!FeedbackService.ROLE_ADMIN.equals(role)) {
            return Result.forbidden("Only administrators can close feedback");
        }
        feedbackService.closeByAdmin(id, userId);
        return Result.success();
    }

    @PostMapping("/admin/{id}/reject")
    public Result<Void> rejectFeedbackAsAdmin(
            @PathVariable Long id,
            @RequestBody FeedbackStatusUpdateRequest request,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        if (!FeedbackService.ROLE_ADMIN.equals(role)) {
            return Result.forbidden("Only administrators can reject feedback");
        }
        feedbackService.rejectByAdmin(id, userId, request == null ? null : request.getReason());
        return Result.success();
    }

    @PostMapping("/admin/{id}/priority")
    public Result<Void> updateFeedbackPriority(
            @PathVariable Long id,
            @RequestBody FeedbackStatusUpdateRequest request,
            @RequestHeader("X-User-Role") String role) {
        if (!FeedbackService.ROLE_ADMIN.equals(role)) {
            return Result.forbidden("Only administrators can update feedback priority");
        }
        feedbackService.updatePriority(id, request == null ? null : request.getPriority());
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<FeedbackDetailVO> getFeedbackDetail(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        return Result.success(feedbackService.getFeedbackDetail(id, userId, role));
    }

    @PostMapping("/{id}/messages")
    public Result<FeedbackDetailVO> replyMyFeedback(
            @PathVariable Long id,
            @RequestBody FeedbackMessageRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return Result.success(feedbackService.replyAsUser(id, request, userId));
    }

    @PostMapping("/{id}/close")
    public Result<Void> closeMyFeedback(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        feedbackService.closeByUser(id, userId);
        return Result.success();
    }

    @PostMapping(value = "/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<FeedbackAttachmentUploadVO> uploadFeedbackAttachment(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId) {
        return Result.success(minioStorageService.uploadFeedbackAttachment(file, userId));
    }

    @GetMapping("/attachments")
    public ResponseEntity<InputStreamResource> downloadFeedbackAttachment(
            @RequestParam String objectKey,
            @RequestParam(required = false) String fileName,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        FeedbackMessageAttachment attachment = feedbackService.verifyAttachmentAccess(objectKey, userId, role);
        var stat = minioStorageService.statObject(objectKey);
        String contentType = StringUtils.hasText(stat.contentType())
                ? stat.contentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String downloadName = StringUtils.hasText(fileName) ? fileName : attachment.getOriginalName();

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(stat.size())
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES).cachePrivate());

        if (!"IMAGE".equals(attachment.getFileType())) {
            builder.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                    .filename(downloadName, StandardCharsets.UTF_8)
                    .build()
                    .toString());
        }

        return builder.body(new InputStreamResource(minioStorageService.getObjectStream(objectKey)));
    }
}
