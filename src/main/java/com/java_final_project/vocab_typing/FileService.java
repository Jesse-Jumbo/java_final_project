package com.java_final_project.vocab_typing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileService {

    private final Path uploadDir = Paths.get("uploads");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FileService() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    /** 1. 限制只能上傳 .json，且 Content-Type 必須是 application/json */
    public void validateJsonFile(MultipartFile file) {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new IllegalArgumentException("檔案為空");
        }
        if (!original.toLowerCase().endsWith(".json")) {
            throw new IllegalArgumentException("只允許上傳 .json 檔案");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.equalsIgnoreCase("application/json")) {
            throw new IllegalArgumentException("錯誤的 Content-Type：" + ct);
        }
    }

    /** 2. 解析 JSON，並檢查必填欄位 */
    public void validateJsonContent(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            JsonNode root = objectMapper.readTree(in);

            // 範例檢查：必須有 name 欄位（字串）與 items 陣列
            if (!root.hasNonNull("userId") || !root.get("userId").isTextual()) {
                throw new IllegalArgumentException("JSON 欄位 userId 不可為空，且必須是字串");
            }
            if (!root.hasNonNull("setName") || !root.get("setName").isTextual()) {
                throw new IllegalArgumentException("JSON 欄位 setName 不可為空，且必須是字串");
            }
            if (!root.has("words") || !root.get("words").isArray()) {
                throw new IllegalArgumentException("JSON 欄位 words 必須存在，且為陣列");
            }

            // 檢查 words 每一筆
            for (JsonNode word : root.get("words")) {
                if (!word.hasNonNull("word")) {
                    throw new IllegalArgumentException("words 中每個元素都要有字串型別的 word 欄位");
                }
                if (!word.hasNonNull("definition")) {
                    throw new IllegalArgumentException("words 中每個元素都要有字串型別的 definition 欄位");
                }
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("無法解析 JSON：" + e.getMessage());
        }
    }

    /** 寫入 uploads 目錄，回傳實際存檔名 */
    public String store(MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "-" + original;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, uploadDir.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return filename;
    }
}

