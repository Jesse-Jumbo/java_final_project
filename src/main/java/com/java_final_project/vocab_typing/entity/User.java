package com.java_final_project.vocab_typing.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private String id;  // 使用 String 型別存放如 user0001

    @Column(nullable = false)
    private String name;

    
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<WordSet> wordSets = new HashSet<>();

    public User() {}

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<WordSet> getWordSets() { return wordSets; }
    public void setWordSets(Set<WordSet> wordSets) { this.wordSets = wordSets; }
}