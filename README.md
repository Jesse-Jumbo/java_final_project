# Java Final Project – Vocabulary Typing Game (Inspired by ZType)

## 遊戲說明
[放個遊戲照片]


[放個連結]

這是一款結合背單字與練習英打的射擊遊戲。玩家需輸入正確的單字，才能發射子彈擊毀隕石。遊戲靈感來自 [ZType](https://zty.pe/)，融合教育與娛樂。


[遊玩過程 gif]

---
## Requirements
- Java 17+
- Maven or Gradle (建議使用 Spring Initializr)
- Spring Boot 3.x

開發工具：IntelliJ IDEA / VSCode / Eclipse

需安裝套件：

```bash
Maven, MySQL, Spring Web, Spring Boot DevTools, Lombok, Thymeleaf
```

---
## 更新說明
- （看有沒有需要，或是 changelog 就夠了）
---
## 遊戲簡介:
遊戲中隕石會隨機出現英文單字，玩家需正確打出該單字，才能發射子彈將隕石擊毀，學習與練打並進。

---
## 畫面說明:
[放畫面照片物件說明]

---
# 遊戲細節：
## 啟動方式:
```shell
git clone git@github.com:Jesse-Jumbo/java_final_project.git
cd java_final_project
./mvnw spring-boot:run
```

瀏覽器開啟 http://localhost:8080 開始遊戲

---
## 遊戲操作：
- 鍵盤輸入英文單字
- 快捷鍵：
    - ESC：暫停遊戲，出現主選單（回到首頁、重新開始）
---

## 單字庫與學習系統
### 預設單字庫
遊戲內建以下單字庫供選擇：
* 基礎單字	300
* 日常用語	600
* 會考單字	1,200
* 統測單字	4,000
* 學測單字	7,000
* 多益 TOEIC	8,000
* 雅思 IELTS	8,000
* 托福 TOEFL	15,000

每個單字皆包含中文解釋，幫助理解與記憶。

---
### 自訂單字庫
玩家可上傳自訂單字庫（如 .txt 或 .csv 檔案），用於個人化學習。

---
## 間隔複習系統（Spaced Repetition）
遊戲內建仿 Anki 的記憶曲線複習機制，幫助玩家長期記憶單字。

- 玩家可設定：
    * 每日練習單字數（如：10、30、50）
    * 每日更新時間（如：08:00 AM）
    * 複習熟悉次數（如：單字需複習 7 次才算熟悉）
    * 起始單字掉落速度（如：3 秒/個）
    * 終點單字掉落速度（如：1 秒/個）
    * 每次出現次數以完成複習（如：每次遊戲至少遇到 2 次該單字）

此系統將根據玩家進度自動安排當日複習單字，強化記憶效果。

---
## 單字庫匯入功能
支援 .txt / .csv / .json 檔案，上傳後自動解析成英文單字 + 中文解釋。

1. 【推薦】.txt 格式（Anki 匯出格式）
   每行格式為：

    ```php-template
    英文單字<TAB>中文解釋
    ```
   例如：

    ```arduino=
    apple	蘋果  
    run	跑步  
    The quick brown fox jumps over the lazy dog	敏捷的棕色狐狸跳過懶狗
    ```

2. .csv 格式
    ```csv=
    word,definition
    apple,蘋果
    run,跑步
    The quick brown fox jumps over the lazy dog,敏捷的棕色狐狸跳過懶狗
    ```

3. JSON 格式（進階使用者或 API）
    ```json=
    [
      {"word": "apple", "definition": "蘋果"},
      {"word": "run", "definition": "跑步"},
      {"word": "The quick brown fox jumps over the lazy dog", "definition": "敏捷的棕色狐狸跳過懶狗"}
    ]
    ```

亦可透過平台內建介面建立、修改單字庫，支援手動輸入與匯出備份。

---

# 遊戲玩法

### 模式一：極速模式
- 隕石掉落速度逐漸提升，挑戰玩家極限打字速度

### 模式二：填空模式
- 隕石上的單字部分被遮蔽，需根據上下文推測拼出正確單字

---
## 過關條件

擊毀所有隕石，分數達標或生存指定時間

---
## 失敗條件
隕石撞擊太空船超過允許次數

---

## 物件設定：
### **`太空船`**
- 固定位置於畫面底部
- 接收玩家打字輸入，發射子彈

---

### **`隕石`**
- 載有待拼寫單字
- 命中玩家超過一定次數，遊戲結束
- 爆炸提示效果
  當玩家正確拼出單字並擊中隕石後，隕石會爆炸，並在畫面上噴出中文解釋文字，加深記憶印象。

  例如：
    - `apple` ➜ 隕石爆炸 ➜ 畫面彈出「蘋果 🍎」
    - `run` ➜ 畫面爆炸 ➜ 彈出「跑步 🏃」

---
### **`子彈`**
- 玩家輸入正確字詞後自動發射
- 一發命中一個隕石

---
# 地圖說明
- 響應式 UI，適應不同螢幕解析度
- 可未來拓展為關卡式（如場景切換）

---
# image sours

---
# sound sours

---
## License
本遊戲專案為學期課程作品，使用者可自由參考或擴充非商業用途。若用於其他用途，請先取得作者許可。

---
## Contributors
1. [Jesse](https://github.com/Jesse-Jumbo)：後端
2. [Mihsia](https://github.com/mihsia0506)：前端
3. [Chingyong](https://github.com/Chingyong0905)：資料庫設計
---

### Contributor Note 20250617
這段文字由 ChingYong 在 `branch-test` 分支中加入，用於練習 Git 分支開發與 Pull Request 流程。
可刪除。