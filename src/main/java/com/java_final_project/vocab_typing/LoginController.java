package com.java_final_project.vocab_typing;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Map;


@Controller
public class LoginController {

    private final RestTemplate restTemplate;

    @Autowired
    public LoginController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 顯示登入頁面 (login.html)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";  // 對應 templates/login.html
    }

// 處理登入表單提交
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam(value = "userName", required = false) String userName,
            HttpSession session,
            Model model) {
        // 檢查是否有輸入內容
        // 1. 基本輸入檢查
        if (userName == null || userName.trim().isEmpty()) {
            model.addAttribute("errorMessage", "請輸入使用者名稱");
            return "login";
        }

        // 2. 呼叫 helper function 做實際認證/查詢
        if (hasUser(userName, session)) {
            // 驗證成功、Session 已設值
            session.setAttribute("userName", userName);
            return "redirect:/home";
        } else {
            // 驗證失敗：分兩種情況設定不同訊息
            Object failReason = session.getAttribute("authFailReason");
            if ("NOT_FOUND".equals(failReason)) {
                createUser(userName);
                model.addAttribute("errorMessage", "使用者不存在，已新增");
            } else {
                createUser(userName);
                model.addAttribute("errorMessage", "無法連線至會員服務，請稍後再試");
            }
            // 清掉暫存的原因
            session.removeAttribute("authFailReason");
            return "redirect:/login";
        }
    }

    /**
     * 呼叫遠端 API 查詢使用者，並根據回傳結果決定是否認證成功。
     * 成功時把原始 JSON 存到 session（key = "userJson"）。
     * 失敗時在 session 記錄失敗原因 ("NOT_FOUND" / "ERROR")。
     */
    private boolean hasUser(String userName, HttpSession session) {
        String url = "http://localhost:8080/api/users/name/{name}";
        try {
            ResponseEntity<String> resp =
                    restTemplate.getForEntity(url, String.class, userName);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                // 認證成功：把 user JSON 存 session
                session.setAttribute("userJson", resp.getBody());
                return true;
            } else if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                // 找不到使用者
                session.setAttribute("authFailReason", "NOT_FOUND");
                return false;
            } else {
                // 其他非 2xx 或 404 的情況
                session.setAttribute("authFailReason", "ERROR");
                return false;
            }
        } catch (RestClientException ex) {
            // 連線／timeout 等錯誤
            session.setAttribute("authFailReason", "ERROR");
            return false;
        }
    }
    /**
     * 呼叫遠端 API 建立新使用者。
     */
    private ResponseEntity<String> createUser(String userName) {
        String url = "http://localhost:8080/api/users";

        // 直接用一個 Map 當 body（會被序列化成 JSON）
        Map<String, String> payload = Collections.singletonMap("name", userName);

        // 呼叫遠端 POST /api/users
        return restTemplate.postForEntity(url, payload, String.class);
    }
}
