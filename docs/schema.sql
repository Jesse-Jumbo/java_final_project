-- 使用者表
CREATE TABLE users (
  user_id VARCHAR(50) PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

-- 單字表
CREATE TABLE words (
  word_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  word VARCHAR(255) NOT NULL,
  definition TEXT
);

-- 單字集表
CREATE TABLE word_sets (
  word_sets_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(50),
  set_name VARCHAR(255),
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 單字集與單字的對應表（多對多關係）
CREATE TABLE word_sets_words (
  word_sets_words_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  word_id BIGINT,
  word_sets_id BIGINT,
  added_at TIMESTAMP,
  FOREIGN KEY (word_id) REFERENCES words(word_id),
  FOREIGN KEY (word_sets_id) REFERENCES word_sets(word_sets_id)
);

-- 個人化設定表
CREATE TABLE personal_config (
  config_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(50) NOT NULL,
  daily_word_limit INT,
  review_threshold INT,
  start_speed_sec INT,
  end_speed_sec INT,
  updated_at TIMESTAMP
  -- 若你想加 FK 可加：FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 遊戲紀錄表
CREATE TABLE game_sessions (
  sessions_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(50),
  game_mode VARCHAR(20),
  score INT,
  duration_sec INT,
  correct_count INT,
  miss_count INT,
  played_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 複習紀錄表
CREATE TABLE review_logs (
  review_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(50) NOT NULL,
  word_id BIGINT NOT NULL,
  set_name VARCHAR(255) NOT NULL,
  review_threshold INT,
  review_count INT,
  next_review_at DATETIME,
  last_review_at DATETIME,
  status VARCHAR(50),
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (word_id) REFERENCES words(word_id)
);
