# 資料庫建構說明（Database Setup）

本專案後端使用 **MySQL** 作為資料庫系統，以下為本地端建構資料庫的完整流程說明。

---

## 步驟 1：建立資料庫

請先啟動你的 MySQL，並建立一個新的資料庫，例如 `vocab_typing`：

```sql
CREATE DATABASE vocab_typing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 步驟 2：匯入資料表結構
請使用下列方式匯入表結構（schema.sql）：
```
mysql -u YOUR_USERNAME -p vocab_typing < docs/schema.sql
```
請將 YOUR_USERNAME 替換為你本機的 MySQL 使用者名稱。系統會提示你輸入密碼。

## 步驟 3：匯入初始測試資料
匯入測試資料以便進行功能測試，可執行以下指令：
```
mysql -u YOUR_USERNAME -p vocab_typing < docs/data.sql
```

## 步驟 4：設定 Spring Boot 資料庫連線
請於 src/main/resources/ 中建立或修改 application.properties 檔案如下：
```
spring.datasource.url=jdbc:mysql://localhost:3306/vocab_typing
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.sql.init.mode=never
```

## 步驟 5：啟動後端服務
使用 IntelliJ 開啟專案後，執行主類別 VocabTypingApplication.java 即可啟動 Spring Boot 後端服務。

