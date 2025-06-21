package com.java_final_project.vocab_typing;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PreviewController {

    private final GameService gameService;  // 或你要用的 service

    public PreviewController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/preview")
    public String previewByGroup(
            @RequestParam("group") String group,
            Model model) {
        // 1. 根據 group 取得要預覽的資料
        var words = gameService.getWordsByGroup(group);
        // 2. 把 group 與資料放到 Model 裡
        model.addAttribute("group", group);
        model.addAttribute("words", words);
        // 3. 回傳 Thymeleaf 模板名稱 preview.html
        return "preview";
    }
}
