package com.java_final_project.vocab_typing;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GamePageController {

    private final GameService gameService;

    public GamePageController(GameService gameService) {
        this.gameService = gameService;
    }

    // 首頁畫面（非遊戲邏輯）
    @GetMapping("/")
    public String home() {
        return "home"; // 對應 templates/home.html
    }

    @PostMapping("/select")
    public String selectGroup(@RequestParam("group") String group, Model model) {
        gameService.setGroup(group);
        return "redirect:/play";
    }

    // 遊戲畫面
    @GetMapping("/play")
    public String play() {
        return "index"; // 對應 templates/index.html，邏輯在 JS 處理
    }
}
