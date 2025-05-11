package com.example.flashcards.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Deck implements Serializable {
    private int id;
    private String name;
    private String description;
    private boolean isFavorite;
    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
