package com.java_final_project.vocab_typing;

import java.util.List;

public class WordRecord {
    public int id;
    public String word;
    public String definition;
    public String group;
    public List<String> history;
    public String nextDate;
    public String status;
    public boolean defeated = false; // 玩家是否已經成功拼完這顆單字
    public int shownCount = 0; // 出現次數

}