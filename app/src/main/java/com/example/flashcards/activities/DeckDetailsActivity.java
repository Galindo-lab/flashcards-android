
package com.example.flashcards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.activities.cardList.CardListActivity;
import com.example.flashcards.models.Deck;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class DeckDetailsActivity extends AppCompatActivity {


    private Deck deck;
    private TextInputEditText etDeckName;
    private TextInputEditText etDeckDescription;
    private TextInputEditText etDeckCategory;
    private SwitchCompat switchFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deck_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        loadDeckData();
        setupToolbar();
    }

    private void initializeViews() {
        etDeckName = findViewById(R.id.etDeckName);
        etDeckDescription = findViewById(R.id.etDeckDescription);
        etDeckCategory = findViewById(R.id.etDeckCategory);
        switchFavorite = findViewById(R.id.switchFavorite);
    }

    private void loadDeckData() {
        deck = (Deck) getIntent().getSerializableExtra("deck_data");

        if (deck == null) {
            Toast.makeText(this, "No se encontró el deck", Toast.LENGTH_SHORT).show();
            return;
        }

        etDeckName.setText(deck.getName() != null ? deck.getName() : "");
        etDeckDescription.setText(deck.getDescription() != null ? deck.getDescription() : "");
        etDeckCategory.setText(deck.getCategory() != null ? deck.getCategory() : "");
        switchFavorite.setChecked(deck.isFavorite());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void saveDeck(View view) {
        try {

            Deck formDeck = getFormData();
            DBHelper dbHelper = new DBHelper(this);
            boolean isNewDeck = deck == null;

            if (isNewDeck) {
                long result = dbHelper.createDeck(formDeck);
                if (result == -1) throw new Exception("Error al crear el deck");
                deck = formDeck;
            } else {
                updateDeckData(formDeck);
                if (dbHelper.updateDeck(deck) == 0) throw new Exception("Error al actualizar el deck");
            }

            navigateToCardList();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateDeckData(Deck newData) {
        deck.setName(newData.getName());
        deck.setDescription(newData.getDescription());
        deck.setCategory(newData.getCategory());
        deck.setFavorite(newData.isFavorite());
    }

    private Deck getFormData() throws Exception {
        String name = etDeckName.getText().toString().trim();
        if (name.isEmpty()) {
            throw new Exception("El nombre del mazo es obligatorio");
        }

        Deck foo = new Deck();

        foo.setName(name);
        foo.setDescription(etDeckDescription.getText().toString().trim());
        foo.setCategory(etDeckCategory.getText().toString().trim());
        foo.setFavorite(switchFavorite.isChecked());

        return foo;
    }

    private void navigateToCardList() {
        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra("deck_data", deck);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}