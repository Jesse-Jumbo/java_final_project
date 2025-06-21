package com.java_final_project.vocab_typing;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
public class UploadController {

    private final FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/upload")
    public String showUploadPage() {
        return "uploadpage";
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleFileUpload(
            @RequestParam("file") MultipartFile file) {
        try {
            // 1. 驗證檔案格式與大小
            fileService.validateJsonFile(file);

            // 2. 解析並檢查內容
            fileService.validateJsonContent(file);

            // 3. 寫入檔案系統
            String filename = fileService.store(file);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "filename", filename
            ));
        } catch (IllegalArgumentException ex) {
            // 拋驗證錯誤，回傳 400
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", ex.getMessage()));
        } catch (Exception ex) {
            // 其他錯誤，回傳 500
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", ex.getMessage()));
        }
    }
}
