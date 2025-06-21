package com.java_final_project.vocab_typing.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "word_sets")
public class WordSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_sets_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "set_name")
    private String setName;

    @OneToMany(mappedBy = "wordSet")
    @JsonIgnore
    private Set<WordSetWord> wordLinks = new HashSet<>();

    public WordSet() {}

    public WordSet(Long id, User user, String setName) {
        this.id = id;
        this.user = user;
        this.setName = setName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSetName() { return setName; }
    public void setSetName(String setName) { this.setName = setName; }

    public Set<WordSetWord> getWordLinks() { return wordLinks; }
    public void setWordLinks(Set<WordSetWord> wordLinks) { this.wordLinks = wordLinks; }
}
