package com.java_final_project.vocab_typing.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long id;

    @Column(nullable = false)
    private String word;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @OneToMany(mappedBy = "word")
    @JsonIgnore
    private Set<WordSetWord> wordSetLinks = new HashSet<>();

    public Word() {}

    public Word(Long id, String word, String definition) {
        this.id = id;
        this.word = word;
        this.definition = definition;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public Set<WordSetWord> getWordSetLinks() { return wordSetLinks; }
    public void setWordSetLinks(Set<WordSetWord> wordSetLinks) { this.wordSetLinks = wordSetLinks; }
}