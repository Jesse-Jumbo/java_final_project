package com.java_final_project.vocab_typing;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GamePageController {

    private final GameService gameService;
    private final WordRepository_jumbo wordRepository;

    public GamePageController(GameService gameService, WordRepository_jumbo wordRepository) {
        this.gameService = gameService;
        this.wordRepository = wordRepository;
    }


    // 首頁畫面（非遊戲邏輯）
    @GetMapping("/")
    public String home(Model model) {
        List<String> groups = wordRepository.getAllGroups();
        model.addAttribute("groups", groups);
        return "home";
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
