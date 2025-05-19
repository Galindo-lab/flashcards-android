package com.example.flashcards.models;

import java.io.Serializable;

public class Card implements Serializable {
    private int id;
    private int deckId;
    private String term;
    private String definition;
    private boolean isFavorite;

    public Card() {

    }

    public Card(int deckId, String term, String definition, boolean isFavorite) {
        this.deckId = deckId;
        this.term = term;
        this.definition = definition;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
