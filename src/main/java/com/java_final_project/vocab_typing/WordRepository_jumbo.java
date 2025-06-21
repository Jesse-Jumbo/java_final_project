package com.java_final_project.vocab_typing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                    clone.history = new ArrayList<>(w.history);
                    clone.nextDate = w.nextDate;
                    clone.status = w.status;
                    return clone;
                }))
                .collect(Collectors.toList());

        Collections.shuffle(repeated);
        return repeated.stream().limit((long)limit * repeat).collect(Collectors.toList());
    }

    public void markReviewedToday(WordRecord word) {
        String today = LocalDate.now().toString();
        if (!word.history.contains(today)) {
            word.history.add(today);
        }

        int reviewCount = word.history.size();
        int[] intervals = {2, 4, 8, 15, 30, 30};

        if (reviewCount <= intervals.length) {
            LocalDate next = LocalDate.now().plusDays(intervals[reviewCount - 1]);
            word.nextDate = next.toString();
        } else {
            word.status = "mastered"; // 畢業
            word.nextDate = null;
        }
    }

    public void commitReviewedWords(List<WordRecord> reviewed, boolean writeToFile) {
        reviewed.stream()
                .map(r -> r.word)
                .distinct()
                .forEach(word -> {
                    allWords.stream()
                            .filter(w -> w.word.equalsIgnoreCase(word))
                            .findFirst()
                            .ifPresent(this::markReviewedToday);
                });
        save();
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
//        try {
//            JsonNode root = objectMapper.readTree(json);
//            if (root.isArray()) {
//                for (JsonNode node : root) {
//                    // 1. 抽出最外層 id
//                    long     id         = node.get("id").asLong();
//                    // 2. 內層 word 物件
//                    JsonNode wordNode   = node.get("word");
//                    // 3. 取出扁平化的屬性
//                    String   theWord    = wordNode.get("word").asText();
//                    String   definition = wordNode.get("definition").asText();
//
//                    // 4. new 出一個乾淨的 WordRecord
//                    WordRecord wr = new WordRecord();
//                    wr.setId(id);
//                    wr.setWord(theWord);
//                    wr.setDefinition(definition);
//                    // （其他欄位保持預設或自己再塞值）
//
//                    allWordsInGroups.add(wr);
//                }
//            }
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
//        } catch(Exception e){
//            throw new RuntimeException("解析失敗", e);
//        }

        return allWordsInGroups;


    }

//    //給瀏覽字卡用
//    public List<WordRecord> previewWordset(String group) {
//        List<WordRecord> wordsInTheSet = allWords.stream()
//                .filter(w -> w.group.equalsIgnoreCase(group))
//                .collect(Collectors.toList());
//
//        return wordsInTheSet;
//    }

}

