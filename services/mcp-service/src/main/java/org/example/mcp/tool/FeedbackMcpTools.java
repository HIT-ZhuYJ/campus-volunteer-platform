package org.example.mcp.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mcp.auth.McpRequestContext;
import org.example.mcp.gateway.CloudDemoGatewayClient;
import org.example.mcp.gateway.GatewayBinaryResponse;
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
public class FeedbackMcpTools {

    private final CloudDemoGatewayClient gatewayClient;
    private final McpRequestContext requestContext;
    private final ObjectMapper objectMapper;

    public FeedbackMcpTools(CloudDemoGatewayClient gatewayClient,
                            McpRequestContext requestContext,
                            ObjectMapper objectMapper) {
        this.gatewayClient = gatewayClient;
        this.requestContext = requestContext;
        this.objectMapper = objectMapper;
    }

    @Tool(description = "Create a feedback ticket for the current user. category must be QUESTION, SUGGESTION, BUG, COMPLAINT, or OTHER. attachmentsJson is optional and should be a JSON array of objects returned by uploadFeedbackAttachment. Requires a logged-in MCP session.")
    public Map<String, Object> createFeedback(String title, String category, String content, String attachmentsJson) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("title", title);
        request.put("category", category);
        request.put("content", content);
        request.put("attachments", parseAttachments(attachmentsJson));

        JsonNode data = gatewayClient.post("/feedback", request, requestContext.requireGatewayToken());
        return toFeedback(data);
    }

    @Tool(description = "List the current user's feedback tickets. Optional status can be OPEN, REPLIED, CLOSED, or REJECTED. Returns paging metadata and feedback summaries. Requires a logged-in MCP session.")
    public Map<String, Object> listMyFeedback(Integer page, Integer size, String status) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(page == null || page < 1 ? 1 : page));
        params.add("size", String.valueOf(size == null || size < 1 ? 10 : Math.min(size, 50)));
        addIfHasText(params, "status", status);

        JsonNode data = gatewayClient.get("/feedback/my", params, requestContext.requireGatewayToken());
        return toPagedFeedback(data, page, size);
    }

    @Tool(description = "Get a feedback detail by feedbackId. Users can only access their own feedback; admins can access any feedback. Returns messages and attachments. Requires a logged-in MCP session.")
    public Map<String, Object> getFeedbackDetail(Long feedbackId) {
        requireId(feedbackId, "feedbackId");
        JsonNode data = gatewayClient.get("/feedback/" + feedbackId, null, requestContext.requireGatewayToken());
        return toFeedback(data);
    }

    @Tool(description = "Reply to one of the current user's open feedback tickets. attachmentsJson is optional and should be a JSON array of objects returned by uploadFeedbackAttachment. Requires a logged-in MCP session.")
    public Map<String, Object> replyMyFeedback(Long feedbackId, String content, String attachmentsJson) {
        requireId(feedbackId, "feedbackId");
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("content", content);
        request.put("attachments", parseAttachments(attachmentsJson));

        JsonNode data = gatewayClient.post("/feedback/" + feedbackId + "/messages", request, requestContext.requireGatewayToken());
        return toFeedback(data);
    }

    @Tool(description = "Close one of the current user's feedback tickets after the issue is resolved. Requires a logged-in MCP session.")
    public Map<String, Object> closeMyFeedback(Long feedbackId) {
        requireId(feedbackId, "feedbackId");
        gatewayClient.post("/feedback/" + feedbackId + "/close", Map.of(), requestContext.requireGatewayToken());
        return Map.of("closed", true, "feedbackId", feedbackId);
    }

    @Tool(description = "Upload a feedback attachment for the current user. Inputs are filename, contentType, and base64Content, which may be raw Base64 or a data URL. Supports JPG, PNG, GIF, WEBP, PDF, Excel, Word, TXT, and CSV. Returns attachment metadata suitable for attachmentsJson. Requires a logged-in MCP session.")
    public Map<String, Object> uploadFeedbackAttachment(String filename, String contentType, String base64Content) {
        requireText(base64Content, "base64Content");
        ParsedBase64File parsedFile = parseBase64File(filename, contentType, base64Content);

        JsonNode data = gatewayClient.postMultipart(
                "/feedback/attachments",
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

    @Tool(description = "Download a bound feedback attachment that the current user is allowed to access. objectKey is required, and fileName is optional. Returns fileName, contentType, byteLength, and base64Content. Requires a logged-in MCP session; admins can download any feedback attachment and volunteers can only download attachments from their own feedback.")
    public Map<String, Object> downloadFeedbackAttachment(String objectKey, String fileName) {
        requireText(objectKey, "objectKey");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("objectKey", objectKey);
        addIfHasText(params, "fileName", fileName);

        GatewayBinaryResponse response = gatewayClient.getBinary(
                "/feedback/attachments",
                params,
                requestContext.requireGatewayToken()
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fileName", response.fileName() != null ? response.fileName() : fileName);
        result.put("contentType", response.contentType());
        result.put("byteLength", response.content().length);
        result.put("base64Content", Base64.getEncoder().encodeToString(response.content()));
        return result;
    }

    @Tool(description = "List feedback tickets for administrator management. Supports page, size, status, category, priority, and keyword filters. Requires an admin MCP session.")
    public Map<String, Object> listAdminFeedback(Integer page, Integer size, String status, String category, String priority, String keyword) {
        requestContext.requireAdmin();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(page == null || page < 1 ? 1 : page));
        params.add("size", String.valueOf(size == null || size < 1 ? 10 : Math.min(size, 50)));
        addIfHasText(params, "status", status);
        addIfHasText(params, "category", category);
        addIfHasText(params, "priority", priority);
        addIfHasText(params, "keyword", keyword);

        JsonNode data = gatewayClient.get("/feedback/admin/list", params, requestContext.requireGatewayToken());
        return toPagedFeedback(data, page, size);
    }

    @Tool(description = "Get feedback detail for administrator management. Returns messages and attachments. Requires an admin MCP session.")
    public Map<String, Object> getAdminFeedbackDetail(Long feedbackId) {
        requestContext.requireAdmin();
        requireId(feedbackId, "feedbackId");

        JsonNode data = gatewayClient.get("/feedback/admin/" + feedbackId, null, requestContext.requireGatewayToken());
        return toFeedback(data);
    }

    @Tool(description = "Reply to a feedback ticket as an administrator. attachmentsJson is optional and should be a JSON array of objects returned by uploadFeedbackAttachment. Requires an admin MCP session.")
    public Map<String, Object> replyFeedbackAsAdmin(Long feedbackId, String content, String attachmentsJson) {
        requestContext.requireAdmin();
        requireId(feedbackId, "feedbackId");

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("content", content);
        request.put("attachments", parseAttachments(attachmentsJson));

        JsonNode data = gatewayClient.post("/feedback/admin/" + feedbackId + "/messages", request, requestContext.requireGatewayToken());
        return toFeedback(data);
    }

    @Tool(description = "Close a feedback ticket as an administrator. Requires an admin MCP session.")
    public Map<String, Object> closeFeedbackAsAdmin(Long feedbackId) {
        requestContext.requireAdmin();
        requireId(feedbackId, "feedbackId");
        gatewayClient.post("/feedback/admin/" + feedbackId + "/close", Map.of(), requestContext.requireGatewayToken());
        return Map.of("closed", true, "feedbackId", feedbackId);
    }

    @Tool(description = "Reject a feedback ticket as an administrator with a required reason. Requires an admin MCP session.")
    public Map<String, Object> rejectFeedbackAsAdmin(Long feedbackId, String reason) {
        requestContext.requireAdmin();
        requireId(feedbackId, "feedbackId");
        requireText(reason, "reason");

        gatewayClient.post("/feedback/admin/" + feedbackId + "/reject", Map.of("reason", reason), requestContext.requireGatewayToken());
        return Map.of("rejected", true, "feedbackId", feedbackId);
    }

    @Tool(description = "Update a feedback ticket priority as an administrator. priority must be LOW, NORMAL, HIGH, or URGENT. Requires an admin MCP session.")
    public Map<String, Object> updateFeedbackPriority(Long feedbackId, String priority) {
        requestContext.requireAdmin();
        requireId(feedbackId, "feedbackId");
        requireText(priority, "priority");

        gatewayClient.post("/feedback/admin/" + feedbackId + "/priority", Map.of("priority", priority), requestContext.requireGatewayToken());
        return Map.of("updated", true, "feedbackId", feedbackId, "priority", priority);
    }

    private Map<String, Object> toPagedFeedback(JsonNode data, Integer requestedPage, Integer requestedSize) {
        List<Map<String, Object>> feedback = new ArrayList<>();
        for (JsonNode item : data.path("records")) {
            feedback.add(toFeedbackSummary(item));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("page", data.path("current").asInt(requestedPage == null ? 1 : requestedPage));
        result.put("size", data.path("size").asInt(requestedSize == null ? 10 : requestedSize));
        result.put("total", data.path("total").asLong());
        result.put("pages", data.path("pages").asLong());
        result.put("feedback", feedback);
        return result;
    }

    private Map<String, Object> toFeedback(JsonNode item) {
        Map<String, Object> feedback = toFeedbackSummary(item);
        List<Map<String, Object>> messages = new ArrayList<>();
        for (JsonNode message : item.path("messages")) {
            messages.add(toMessage(message));
        }
        feedback.put("messages", messages);
        return feedback;
    }

    private Map<String, Object> toFeedbackSummary(JsonNode item) {
        Map<String, Object> feedback = new LinkedHashMap<>();
        feedback.put("id", item.hasNonNull("id") ? item.path("id").asLong() : null);
        feedback.put("userId", item.hasNonNull("userId") ? item.path("userId").asLong() : null);
        feedback.put("title", textOrNull(item, "title"));
        feedback.put("category", textOrNull(item, "category"));
        feedback.put("status", textOrNull(item, "status"));
        feedback.put("priority", textOrNull(item, "priority"));
        feedback.put("lastMessageTime", textOrNull(item, "lastMessageTime"));
        feedback.put("lastReplierRole", textOrNull(item, "lastReplierRole"));
        feedback.put("closedBy", item.hasNonNull("closedBy") ? item.path("closedBy").asLong() : null);
        feedback.put("closedTime", textOrNull(item, "closedTime"));
        feedback.put("rejectReason", textOrNull(item, "rejectReason"));
        feedback.put("createTime", textOrNull(item, "createTime"));
        feedback.put("updateTime", textOrNull(item, "updateTime"));
        return feedback;
    }

    private Map<String, Object> toMessage(JsonNode item) {
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("id", item.hasNonNull("id") ? item.path("id").asLong() : null);
        message.put("feedbackId", item.hasNonNull("feedbackId") ? item.path("feedbackId").asLong() : null);
        message.put("senderId", item.hasNonNull("senderId") ? item.path("senderId").asLong() : null);
        message.put("senderRole", textOrNull(item, "senderRole"));
        message.put("content", textOrNull(item, "content"));
        message.put("messageType", textOrNull(item, "messageType"));
        message.put("createTime", textOrNull(item, "createTime"));
        List<Map<String, Object>> attachments = new ArrayList<>();
        for (JsonNode attachment : item.path("attachments")) {
            attachments.add(toAttachment(attachment));
        }
        message.put("attachments", attachments);
        return message;
    }

    private Map<String, Object> toAttachment(JsonNode item) {
        Map<String, Object> attachment = new LinkedHashMap<>();
        attachment.put("attachmentKey", textOrNull(item, "attachmentKey"));
        attachment.put("fileName", textOrNull(item, "fileName"));
        attachment.put("contentType", textOrNull(item, "contentType"));
        attachment.put("fileSize", item.hasNonNull("fileSize") ? item.path("fileSize").asLong() : 0L);
        attachment.put("fileType", textOrNull(item, "fileType"));
        attachment.put("url", textOrNull(item, "url"));
        return attachment;
    }

    private List<Map<String, Object>> parseAttachments(String attachmentsJson) {
        if (attachmentsJson == null || attachmentsJson.isBlank()) {
            return List.of();
        }
        try {
            JsonNode root = objectMapper.readTree(attachmentsJson);
            if (root.isObject() && root.has("attachment")) {
                root = root.path("attachment");
            }
            if (root.isObject()) {
                return List.of(toAttachment(root));
            }
            if (!root.isArray()) {
                throw new IllegalArgumentException("attachmentsJson must be a JSON array or object");
            }
            List<Map<String, Object>> attachments = new ArrayList<>();
            for (JsonNode item : root) {
                attachments.add(toAttachment(item));
            }
            return attachments;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("attachmentsJson is not valid JSON", ex);
        }
    }

    private ParsedBase64File parseBase64File(String filename, String contentType, String base64Content) {
        String actualFilename = filename == null || filename.isBlank() ? "feedback-attachment" : filename.trim();
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
            case "image/jpeg" -> ".jpg";
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
            default -> ".bin";
        };
    }

    private void addIfHasText(MultiValueMap<String, String> params, String key, String value) {
        if (value != null && !value.isBlank()) {
            params.add(key, value.trim());
        }
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

    private record ParsedBase64File(String filename, String contentType, byte[] content) {
    }
}
