package com.example.flashcards.activities.deckList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.activities.DeckDetailsActivity;
import com.example.flashcards.activities.cardList.CardListActivity;
import com.example.flashcards.models.Deck;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class DeckListActivity extends AppCompatActivity implements DeckAdapter.OnDeckClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deck_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Habilitar botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        loadDecks();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Recargar los decks cuando la actividad se reinicie
        loadDecks();
    }

    private void loadDecks() {
        RecyclerView recyclerView;
        List<Deck> decks;

        try (DBHelper dbHelper = new DBHelper(this)) {
            recyclerView = findViewById(R.id.deck_recycler_view);
            recyclerView.setHasFixedSize(true);
            decks = dbHelper.getAllDecks();
        }

        DeckAdapter adapter = new DeckAdapter(decks, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDeckClick(Deck deck) {
        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra("deck_data", deck);

        startActivity(intent);
    }

    public void floatingButtonOnClick(View view) {
        Intent intent = new Intent(this, DeckDetailsActivity.class);
        startActivity(intent);
    }
}