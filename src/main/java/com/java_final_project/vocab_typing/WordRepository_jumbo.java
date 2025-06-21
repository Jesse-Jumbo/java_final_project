package com.java_final_project.vocab_typing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class WordRepository_jumbo {
    private static final String FILE_PATH = "src/main/resources/vocab_review.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<WordRecord> allWords;
    private final RestTemplate restTemplate;//Allgroup

    public WordRepository_jumbo(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;//Allgroup
        load();
    }


    public void load() {
        try {
            allWords = objectMapper.readValue(new File(FILE_PATH), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("讀取 vocab_review.json 失敗", e);
        }
    }

    public void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), allWords);
        } catch (Exception e) {
            throw new RuntimeException("儲存 vocab_review.json 失敗", e);
        }
    }

    public List<WordRecord> getTodayWords(String group, int limit, int repeat) {
        List<WordRecord> repeated = allWords.stream()
                .filter(w -> w.group.equalsIgnoreCase(group))
                .filter(w ->{
                    if (!w.status.equals("in-progress")) return false;
                    try {
                        return !LocalDate.parse(w.nextDate).isAfter(LocalDate.now()); // nextDate <= today
                    } catch (Exception e) {
                        return false;
                    }
                })
                .flatMap(w -> IntStream.range(0, repeat).mapToObj(i -> {
                    WordRecord clone = new WordRecord();
                    clone.word = w.word;
                    clone.definition = w.definition;
                    clone.group = w.group;
                    clone.review_count = w.review_count;
                    clone.nextDate = w.nextDate;
                    clone.status = w.status;
                    return clone;
                }))
                .collect(Collectors.toList());

        Collections.shuffle(repeated);
        return repeated.stream().limit((long)limit * repeat).collect(Collectors.toList());
    }

    public void commitReviewedWords(List<WordRecord> reviewed, boolean writeToFile) {
        String userName = reviewed.isEmpty() ? null : reviewed.get(0).userName; // 從 WordRecord 拿使用者名
        if (userName == null) return;

        // 定義遺忘曲線間隔
        int[] intervals = {2, 4, 8, 15, 30, 30};

        List<Map<String, Object>> requestBody = reviewed.stream()
                .map(wr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("setName", wr.group);
                    map.put("word", wr.word);
                    map.put("definition", wr.definition);

                    // 當天複習
                    LocalDateTime lastReviewAt = LocalDate.now().atStartOfDay();
                    map.put("lastReviewAt", lastReviewAt.toString());

                    // 計算下一次複習日期
                    int reviewCount = wr.review_count;
                    reviewCount += 1; // 存入今天 +1

                    if (reviewCount <= intervals.length) {
                        LocalDateTime nextReviewAt = lastReviewAt.plusDays(intervals[reviewCount - 1]);
                        map.put("nextReviewAt", nextReviewAt.toString());
                    } else {
                        map.put("nextReviewAt", null); // 已畢業
                    }

                    return map;
                })
                .collect(Collectors.toList());

        try {
            String url = String.format("http://localhost:8080/api/review/update?name=%s", userName);
            restTemplate.postForEntity(url, requestBody, String.class);
        } catch (Exception e) {
            System.err.println("❌ 寫入 review_logs 失敗：" + e.getMessage());
        }

        if (writeToFile) {
            save();
        }
    }

    public List<String> getAllGroups(String userName) {
        // 1. 呼叫 API 拿到 JSON 字串
        String url = "http://localhost:8080/api/wordsets/user/" + userName;
        String json = restTemplate.getForObject(url, String.class);

        // 2. 用 ObjectMapper 解析
        List<String> allGroups = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    JsonNode n = node.get("setName");
                    if (n != null && !n.isNull()) {
                        allGroups.add(n.asText());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析單字集 JSON 失敗", e);
        }

        return allGroups;
    }

    //給瀏覽字卡用
    public List<WordRecord> previewWordset(String group) {
        String url = "http://localhost:8080/api/wordset-words/by-set-name/" + group;
        String json = restTemplate.getForObject(url, String.class);

        List<WordRecord> allWordsInGroups = new ArrayList<>();
            try {
                JsonNode root = objectMapper.readTree(json);
                if (!root.isArray()) return allWordsInGroups;

                for (JsonNode node : root) {
                    ObjectNode flat = objectMapper.createObjectNode();

                    if (node.has("word")) {
                        // 1. 先把 word 物件裡的所有欄位攤平進 flat
                        flat.setAll((ObjectNode) node.get("word"));
                        // 2. 如果你也想保留最外層的 id
                        flat.set("id", node.get("id"));
                    } else {
                        // 如果沒有 word，直接把 node 轉成 ObjectNode
                        flat.setAll((ObjectNode) node);
                    }

                    // 3. 轉成 WordRecord
                    WordRecord wr = objectMapper.treeToValue(flat, WordRecord.class);
                    allWordsInGroups.add(wr);
                }
            } catch (Exception e) {
                throw new RuntimeException("解析失敗", e);
            }

        return allWordsInGroups;


    }
}

