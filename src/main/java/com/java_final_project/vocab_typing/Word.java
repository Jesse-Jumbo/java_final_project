package com.java_final_project.vocab_typing;

// record 是 Java 16 起正式支援的語法，用來快速定義 immutable 的資料結構
public record Word(String word, String definition) {
}
