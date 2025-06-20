// 所屬的 package，讓 Spring Boot 能自動掃描與正確引入
package com.java_final_project.vocab_typing.dto;

// 匯入 Word 類別，因為這個 DTO 裡包含 Word 清單
import com.java_final_project.vocab_typing.entity.Word;

import java.util.List; // 匯入 List 類型（Java 集合）

//這是一個 DTO（資料傳輸物件），用來接收從前端上傳來的 JSON 結構資料
public class WordImportRequest {

    //使用者的 ID（例如 user0002）
    private String userId;

    //使用者給這次匯入命名的詞庫名稱（例如「我的單字集」）
    private String setName;

    //要匯入的一批單字清單，每一個都是 Word 物件（包含 word 和 definition）
    private List<Word> words;

    //無參數建構子（Spring Framework 建立物件時需要）
    public WordImportRequest() {}

    //以下是 getter 和 setter 方法，Spring 會用來接收 JSON 的欄位值

    // 取得 userId
    public String getUserId() {
        return userId;
    }

    // 設定 userId
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 取得 setName
    public String getSetName() {
        return setName;
    }

    // 設定 setName
    public void setSetName(String setName) {
        this.setName = setName;
    }

    // 取得要匯入的單字清單
    public List<Word> getWords() {
        return words;
    }

    // 設定單字清單
    public void setWords(List<Word> words) {
        this.words = words;
    }
}
