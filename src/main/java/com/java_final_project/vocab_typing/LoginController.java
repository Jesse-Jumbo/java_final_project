package com.java_final_project.vocab_typing;

Dimport jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

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
        if (userName != null && !userName.trim().isEmpty()) {
            // TODO: 把 userName 存進資料庫
            //session.setAttribute("userName", userName);
            return "redirect:/home";
        }

        // 如果沒輸入，回到登入頁並顯示錯誤
        model.addAttribute("errorMessage", "請輸入使用者名稱");
        return "login";
    }
}
