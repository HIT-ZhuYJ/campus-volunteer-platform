package org.example.mcp.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mcp.auth.McpRequestContext;
import org.example.mcp.gateway.CloudDemoGatewayClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AnnouncementMcpTools {

    private final CloudDemoGatewayClient gatewayClient;
    private final McpRequestContext requestContext;
    private final ObjectMapper objectMapper;

    public AnnouncementMcpTools(CloudDemoGatewayClient gatewayClient,
                                McpRequestContext requestContext,
                                ObjectMapper objectMapper) {
        this.gatewayClient = gatewayClient;
        this.requestContext = requestContext;
        this.objectMapper = objectMapper;
    }

    @Tool(description = "List homepage announcements. Use this when the user asks for current platform announcements, notices, or the default homepage content. The limit defaults to 5 and is capped at 20. Returns announcement summaries including image URLs, linked activityIds/activities, and attachments. Requires a logged-in MCP session.")
    public List<Map<String, Object>> listHomeAnnouncements(Integer limit) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("limit", String.valueOf(limit == null || limit < 1 ? 5 : Math.min(limit, 20)));

        JsonNode data = gatewayClient.get("/announcement/home", params, requestContext.requireGatewayToken());
        return toAnnouncementList(data);
    }

    @Tool(description = "List published announcements. Use this when the user wants to browse announcements with pagination. Supports page and size; size is capped at 50. Returns paging metadata and announcement summaries including image URLs, linked activityIds/activities, and attachments. Requires a logged-in MCP session.")
    public Map<String, Object> listAnnouncements(Integer page, Integer size) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(page == null || page < 1 ? 1 : page));
        params.add("size", String.valueOf(size == null || size < 1 ? 10 : Math.min(size, 50)));

        JsonNode data = gatewayClient.get("/announcement/list", params, requestContext.requireGatewayToken());
        return toPagedAnnouncements(data, page, size);
    }

    @Tool(description = "Get a published announcement detail by announcementId. Use this when the user asks to read a specific announcement, including its content, images, status, publish time, linked activityIds/activities, and attachments. Requires a logged-in MCP session.")
    public Map<String, Object> getAnnouncementDetail(Long announcementId) {
        requireId(announcementId, "announcementId");
        JsonNode data = gatewayClient.get("/announcement/" + announcementId, null, requestContext.requireGatewayToken());
        return toAnnouncement(data);
    }

    @Tool(description = "List announcements for administrator management. Use this when an admin wants to review all announcements, including offline ones. Supports page, size, and optional status such as PUBLISHED or OFFLINE. Returns paging metadata and announcement records. Requires an admin MCP session.")
    public Map<String, Object> listAdminAnnouncements(Integer page, Integer size, String status) {
        requestContext.requireAdmin();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(page == null || page < 1 ? 1 : page));
        params.add("size", String.valueOf(size == null || size < 1 ? 10 : Math.min(size, 50)));
        addIfHasText(params, "status", status);

        JsonNode data = gatewayClient.get("/announcement/admin/list", params, requestContext.requireGatewayToken());
        return toPagedAnnouncements(data, page, size);
    }

    @Tool(description = "Get an announcement detail for administrator management, including offline records. Use this before editing or auditing a specific announcement. Requires an admin MCP session.")
    public Map<String, Object> getAdminAnnouncementDetail(Long announcementId) {
        requestContext.requireAdmin();
        requireId(announcementId, "announcementId");

        JsonNode data = gatewayClient.get("/announcement/admin/" + announcementId, null, requestContext.requireGatewayToken());
        return toAnnouncement(data);
    }

    @Tool(description = "Create a new announcement as an administrator. Use this only after the title and content are known. Optional fields include activityId for legacy single-activity linking, activityIdsCsv for multiple linked activities, status, sortOrder, imageKey, imageKeysCsv, attachmentsJson, and attachmentKeysCsv. attachmentsJson should be a JSON array of objects with attachmentKey, fileName, contentType, and fileSize, usually from uploadAnnouncementAttachment. Returns created=true and the submitted title. Requires an admin MCP session.")
    public Map<String, Object> createAnnouncement(
            String title,
            String content,
            Long activityId,
            String activityIdsCsv,
            String status,
            Integer sortOrder,
            String imageKey,
            String imageKeysCsv,
            String attachmentsJson,
            String attachmentKeysCsv
    ) {
        requestContext.requireAdmin();

        Map<String, Object> request = buildAnnouncementRequest(
                title,
                content,
                activityId,
                activityIdsCsv,
                status,
                sortOrder,
                imageKey,
                imageKeysCsv,
                attachmentsJson,
                attachmentKeysCsv
        );

        gatewayClient.post("/announcement/admin", request, requestContext.requireGatewayToken());
        return Map.of("created", true, "title", title);
    }

    @Tool(description = "Update an existing announcement as an administrator. announcementId is required. Provide the new title, content, optional activityId for legacy single-activity linking, activityIdsCsv for multiple linked activities, status, sortOrder, imageKey, imageKeysCsv, attachmentsJson, and attachmentKeysCsv. attachmentsJson should be a JSON array of objects with attachmentKey, fileName, contentType, and fileSize. Returns updated=true and the announcementId. Requires an admin MCP session.")
    public Map<String, Object> updateAnnouncement(
            Long announcementId,
            String title,
            String content,
            Long activityId,
            String activityIdsCsv,
            String status,
            Integer sortOrder,
            String imageKey,
            String imageKeysCsv,
            String attachmentsJson,
            String attachmentKeysCsv
    ) {
        requestContext.requireAdmin();
        requireId(announcementId, "announcementId");

        Map<String, Object> request = buildAnnouncementRequest(
                title,
                content,
                activityId,
                activityIdsCsv,
                status,
                sortOrder,
                imageKey,
                imageKeysCsv,
                attachmentsJson,
                attachmentKeysCsv
        );

        gatewayClient.put("/announcement/admin/" + announcementId, request, requestContext.requireGatewayToken());
        return Map.of("updated", true, "announcementId", announcementId);
    }

    @Tool(description = "Publish an existing announcement as an administrator. Use this to bring an offline announcement back online. announcementId is required. Returns published=true and the announcementId. Requires an admin MCP session.")
    public Map<String, Object> publishAnnouncement(Long announcementId) {
        requestContext.requireAdmin();
        requireId(announcementId, "announcementId");

        gatewayClient.post("/announcement/admin/" + announcementId + "/publish", Map.of(), requestContext.requireGatewayToken());
        return Map.of("published", true, "announcementId", announcementId);
    }

    @Tool(description = "Take an existing announcement offline as an administrator. Use this when an announcement should no longer appear on the homepage or published list but should remain in records. announcementId is required. Returns offline=true and the announcementId. Requires an admin MCP session.")
    public Map<String, Object> offlineAnnouncement(Long announcementId) {
        requestContext.requireAdmin();
        requireId(announcementId, "announcementId");

        gatewayClient.post("/announcement/admin/" + announcementId + "/offline", Map.of(), requestContext.requireGatewayToken());
        return Map.of("offline", true, "announcementId", announcementId);
    }

    @Tool(description = "Delete an announcement as an administrator. Use this only when the announcement should be permanently removed rather than taken offline. announcementId is required. Returns deleted=true and the announcementId. Requires an admin MCP session.")
    public Map<String, Object> deleteAnnouncement(Long announcementId) {
        requestContext.requireAdmin();
        requireId(announcementId, "announcementId");

        gatewayClient.delete("/announcement/admin/" + announcementId, requestContext.requireGatewayToken());
        return Map.of("deleted", true, "announcementId", announcementId);
    }

    @Tool(description = "Upload an announcement image as an administrator. Use this before createAnnouncement or updateAnnouncement when an image must first be stored by the backend. Inputs are filename, contentType, and base64Content. base64Content may be raw Base64 or a data URL. Supports JPG, PNG, GIF, and WEBP. Returns imageKey, imageUrl, normalized filename, contentType, and byteLength. Requires an admin MCP session.")
    public Map<String, Object> uploadAnnouncementImage(
            String filename,
            String contentType,
            String base64Content
    ) {
        requestContext.requireAdmin();
        requireText(base64Content, "base64Content");

        ParsedBase64File parsedFile = parseBase64File(filename, contentType, base64Content);
        JsonNode data = gatewayClient.postMultipart(
                "/announcement/admin/image",
                "file",
                parsedFile.content(),
                parsedFile.filename(),
                parsedFile.contentType(),
                requestContext.requireGatewayToken()
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("imageKey", textOrNull(data, "imageKey"));
        result.put("imageUrl", textOrNull(data, "imageUrl"));
        result.put("filename", parsedFile.filename());
        result.put("contentType", parsedFile.contentType());
        result.put("byteLength", parsedFile.content().length);
        return result;
    }

    @Tool(description = "Upload an announcement attachment as an administrator. Use this before createAnnouncement or updateAnnouncement when an attachment such as PDF, Excel, Word, TXT, or CSV must first be stored by the backend. Inputs are filename, contentType, and base64Content. base64Content may be raw Base64 or a data URL. Returns attachmentKey, fileName, contentType, fileSize, url, and an attachment object suitable for attachmentsJson. Requires an admin MCP session.")
    public Map<String, Object> uploadAnnouncementAttachment(
            String filename,
            String contentType,
            String base64Content
    ) {
        requestContext.requireAdmin();
        requireText(base64Content, "base64Content");

        ParsedBase64File parsedFile = parseBase64File(filename, contentType, base64Content, "announcement-attachment");
        JsonNode data = gatewayClient.postMultipart(
                "/announcement/admin/attachment",
                "file",
                parsedFile.content(),
                parsedFile.filename(),
                parsedFile.contentType(),
                requestContext.requireGatewayToken()
        );

        Map<String, Object> attachment = toAttachment(data);
        Map<String, Object> result = new LinkedHashMap<>(attachment);
        result.put("attachment", attachment);
        result.put("byteLength", parsedFile.content().length);
        return result;
    }

    private Map<String, Object> buildAnnouncementRequest(
            String title,
            String content,
            Long activityId,
            String activityIdsCsv,
            String status,
            Integer sortOrder,
            String imageKey,
            String imageKeysCsv,
            String attachmentsJson,
            String attachmentKeysCsv
    ) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("title", title);
        request.put("content", content);
        request.put("activityId", activityId);
        request.put("activityIds", parseActivityIds(activityId, activityIdsCsv));
        request.put("status", status);
        request.put("sortOrder", sortOrder);
        request.put("imageKey", imageKey);
        request.put("imageKeys", splitCsv(imageKeysCsv));
        request.put("attachments", parseAttachments(attachmentsJson, attachmentKeysCsv));
        return request;
    }

    private Map<String, Object> toPagedAnnouncements(JsonNode data, Integer requestedPage, Integer requestedSize) {
        List<Map<String, Object>> announcements = new ArrayList<>();
        for (JsonNode item : data.path("records")) {
            announcements.add(toAnnouncement(item));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("page", data.path("current").asInt(requestedPage == null ? 1 : requestedPage));
        result.put("size", data.path("size").asInt(requestedSize == null ? 10 : requestedSize));
        result.put("total", data.path("total").asLong());
        result.put("pages", data.path("pages").asLong());
        result.put("announcements", announcements);
        return result;
    }

    private List<Map<String, Object>> toAnnouncementList(JsonNode data) {
        List<Map<String, Object>> announcements = new ArrayList<>();
        for (JsonNode item : data) {
            announcements.add(toAnnouncement(item));
        }
        return announcements;
    }

    private Map<String, Object> toAnnouncement(JsonNode item) {
        Map<String, Object> announcement = new LinkedHashMap<>();
        announcement.put("id", item.path("id").asLong());
        announcement.put("title", textOrNull(item, "title"));
        announcement.put("content", textOrNull(item, "content"));
        announcement.put("imageKey", textOrNull(item, "imageKey"));
        announcement.put("imageUrl", textOrNull(item, "imageUrl"));
        announcement.put("imageKeys", toStringList(item.path("imageKeys")));
        announcement.put("imageUrls", toStringList(item.path("imageUrls")));
        announcement.put("activityId", item.hasNonNull("activityId") ? item.path("activityId").asLong() : null);
        announcement.put("activityIds", toLongList(item.path("activityIds")));
        announcement.put("activities", toActivityList(item.path("activities")));
        announcement.put("attachments", toAttachmentList(item.path("attachments")));
        announcement.put("status", textOrNull(item, "status"));
        announcement.put("sortOrder", item.hasNonNull("sortOrder") ? item.path("sortOrder").asInt() : null);
        announcement.put("publisherId", item.hasNonNull("publisherId") ? item.path("publisherId").asLong() : null);
        announcement.put("publishTime", textOrNull(item, "publishTime"));
        announcement.put("createTime", textOrNull(item, "createTime"));
        announcement.put("updateTime", textOrNull(item, "updateTime"));
        return announcement;
    }

    private List<Long> parseActivityIds(Long activityId, String activityIdsCsv) {
        List<Long> values = new ArrayList<>();
        for (String rawId : splitCsv(activityIdsCsv)) {
            try {
                long parsed = Long.parseLong(rawId);
                if (parsed > 0 && !values.contains(parsed)) {
                    values.add(parsed);
                }
            } catch (NumberFormatException ignored) {
                throw new IllegalArgumentException("activityIdsCsv must contain numeric activity IDs");
            }
        }
        if (values.isEmpty() && activityId != null && activityId > 0) {
            values.add(activityId);
        }
        return values;
    }

    private List<Map<String, Object>> parseAttachments(String attachmentsJson, String attachmentKeysCsv) {
        if (attachmentsJson != null && !attachmentsJson.isBlank()) {
            try {
                JsonNode root = objectMapper.readTree(attachmentsJson);
                if (root.isObject() && root.has("attachment")) {
                    root = root.path("attachment");
                }
                if (root.isObject()) {
                    Map<String, Object> attachment = toAttachment(root);
                    return attachment.get("attachmentKey") == null ? List.of() : List.of(attachment);
                }
                if (!root.isArray()) {
                    throw new IllegalArgumentException("attachmentsJson must be a JSON array or object");
                }
                List<Map<String, Object>> attachments = new ArrayList<>();
                for (JsonNode item : root) {
                    Map<String, Object> attachment = toAttachment(item);
                    if (attachment.get("attachmentKey") != null) {
                        attachments.add(attachment);
                    }
                }
                return attachments;
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new IllegalArgumentException("attachmentsJson is not valid JSON", ex);
            }
        }

        List<Map<String, Object>> attachments = new ArrayList<>();
        for (String attachmentKey : splitCsv(attachmentKeysCsv)) {
            Map<String, Object> attachment = new LinkedHashMap<>();
            attachment.put("attachmentKey", attachmentKey);
            attachment.put("fileName", filenameFromObjectKey(attachmentKey));
            attachment.put("contentType", null);
            attachment.put("fileSize", 0L);
            attachments.add(attachment);
        }
        return attachments;
    }

    private List<Map<String, Object>> toActivityList(JsonNode arrayNode) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return List.of();
        }
        List<Map<String, Object>> activities = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            Map<String, Object> activity = new LinkedHashMap<>();
            activity.put("id", item.hasNonNull("id") ? item.path("id").asLong() : null);
            activity.put("title", textOrNull(item, "title"));
            activity.put("location", textOrNull(item, "location"));
            activity.put("startTime", textOrNull(item, "startTime"));
            activity.put("endTime", textOrNull(item, "endTime"));
            activity.put("status", textOrNull(item, "status"));
            activity.put("category", textOrNull(item, "category"));
            activities.add(activity);
        }
        return activities;
    }

    private List<Map<String, Object>> toAttachmentList(JsonNode arrayNode) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return List.of();
        }
        List<Map<String, Object>> attachments = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            attachments.add(toAttachment(item));
        }
        return attachments;
    }

    private Map<String, Object> toAttachment(JsonNode item) {
        Map<String, Object> attachment = new LinkedHashMap<>();
        attachment.put("attachmentKey", textOrNull(item, "attachmentKey"));
        attachment.put("fileName", textOrNull(item, "fileName"));
        attachment.put("contentType", textOrNull(item, "contentType"));
        attachment.put("fileSize", item.hasNonNull("fileSize") ? item.path("fileSize").asLong() : 0L);
        attachment.put("url", textOrNull(item, "url"));
        return attachment;
    }

    private void addIfHasText(MultiValueMap<String, String> params, String key, String value) {
        if (value != null && !value.isBlank()) {
            params.add(key, value.trim());
        }
    }

    private List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (String item : csv.split(",")) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }
        return values;
    }

    private void requireId(Long id, String fieldName) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private String textOrNull(JsonNode node, String fieldName) {
        return node.hasNonNull(fieldName) ? node.path(fieldName).asText() : null;
    }

    private List<String> toStringList(JsonNode arrayNode) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return List.of();
        }

        List<String> values = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            if (!item.isNull()) {
                values.add(item.asText());
            }
        }
        return values;
    }

    private List<Long> toLongList(JsonNode arrayNode) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return List.of();
        }

        List<Long> values = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            if (item.canConvertToLong()) {
                values.add(item.asLong());
            }
        }
        return values;
    }

    private ParsedBase64File parseBase64File(String filename, String contentType, String base64Content) {
        return parseBase64File(filename, contentType, base64Content, "announcement-image");
    }

    private ParsedBase64File parseBase64File(String filename, String contentType, String base64Content, String defaultFilename) {
        String actualFilename = filename == null || filename.isBlank() ? defaultFilename : filename.trim();
        String actualContentType = contentType == null ? "" : contentType.trim();
        String actualBase64 = base64Content.trim();

        if (actualBase64.startsWith("data:")) {
            int semicolonIndex = actualBase64.indexOf(';');
            int commaIndex = actualBase64.indexOf(',');
            if (semicolonIndex > 5 && commaIndex > semicolonIndex) {
                if (actualContentType.isBlank()) {
                    actualContentType = actualBase64.substring(5, semicolonIndex);
                }
                actualBase64 = actualBase64.substring(commaIndex + 1);
            }
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(actualBase64);
            return new ParsedBase64File(
                    ensureFilenameExtension(actualFilename, actualContentType),
                    actualContentType.isBlank() ? "application/octet-stream" : actualContentType,
                    decoded
            );
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("base64Content is not valid Base64", ex);
        }
    }

    private String ensureFilenameExtension(String filename, String contentType) {
        if (filename.contains(".")) {
            return filename;
        }
        return filename + switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "application/pdf" -> ".pdf";
            case "application/vnd.ms-excel" -> ".xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> ".xlsx";
            case "application/msword" -> ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
            case "text/plain" -> ".txt";
            case "text/csv" -> ".csv";
            case "", "application/octet-stream" -> ".bin";
            default -> ".jpg";
        };
    }

    private String filenameFromObjectKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return "attachment";
        }
        int slashIndex = objectKey.lastIndexOf('/');
        return slashIndex >= 0 && slashIndex < objectKey.length() - 1
                ? objectKey.substring(slashIndex + 1)
                : objectKey;
    }

    private record ParsedBase64File(String filename, String contentType, byte[] content) {
    }
}
