package com.java_final_project.vocab_typing;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameController {

    @GetMapping("/")
    public String home(Model model) {
        // 假資料
        model.addAttribute("word", "apple");
        model.addAttribute("definition", "蘋果");
        return "index"; // 對應 index.html
    }

    @GetMapping("/play")
    public String play(Model model) {
        // 假資料
        model.addAttribute("word", "apple");
        model.addAttribute("definition", "蘋果");
        return "play"; // 對應 play.html
    }

}
