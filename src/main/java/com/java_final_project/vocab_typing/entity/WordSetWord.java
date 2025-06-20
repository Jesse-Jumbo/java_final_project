package com.java_final_project.vocab_typing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "word_sets_words")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordSetWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_sets_words_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;

    @ManyToOne
    @JoinColumn(name = "word_sets_id")
    private WordSet wordSet;

    @Column(name = "added_at")
    private Timestamp addedAt;

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

	public void setWord(Word word2) {
	    this.word = word2;
	}

	public void setWordSet(WordSet savedSet) {
	    this.wordSet = savedSet;
	}
	
	public Word getWord() {
	    return this.word;
	}

	public WordSet getWordSet() {
	    return this.wordSet;
	}

	public Timestamp getAddedAt() {
	    return this.addedAt;
	}

}
