package com.example.flashcards.activities.deckList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

public class DeckListActivity extends AppCompatActivity implements DeckAdapter.OnDeckClickListener {

    private Spinner categorySpinner;
    private RecyclerView recyclerView;
    private DeckAdapter adapter;
    private List<Deck> allDecks;

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

        // Inicializar vistas
        categorySpinner = findViewById(R.id.category_spinner);
        recyclerView = findViewById(R.id.deck_recycler_view);
        recyclerView.setHasFixedSize(true);

        // Cargar decks y configurar spinner
        loadDecksAndCategories();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Recargar los decks cuando la actividad se reinicie
        loadDecksAndCategories();
    }

    private void loadDecksAndCategories() {
        try (DBHelper dbHelper = new DBHelper(this)) {
            // Obtener todos los decks
            allDecks = dbHelper.getAllDecks();

            // Obtener categorías únicas
            List<String> categories = new ArrayList<>();
            categories.add("Todas las categorías"); // Opción por defecto

            for (Deck deck : allDecks) {
                if (deck.getCategory() != null && !deck.getCategory().isEmpty()
                        && !categories.contains(deck.getCategory())) {
                    categories.add(deck.getCategory());
                }
            }

            // Configurar el spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, categories);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);

            // Mostrar todos los decks inicialmente
            adapter = new DeckAdapter(allDecks, this);
            recyclerView.setAdapter(adapter);

            // Listener para el spinner
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCategory = parent.getItemAtPosition(position).toString();
                    filterDecksByCategory(selectedCategory);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No hacer nada
                }
            });
        }
    }

    private void filterDecksByCategory(String category) {
        List<Deck> filteredDecks = new ArrayList<>();

        if (category.equals("Todas las categorías")) {
            filteredDecks.addAll(allDecks);
        } else {
            for (Deck deck : allDecks) {
                if (category.equals(deck.getCategory())) {
                    filteredDecks.add(deck);
                }
            }
        }

        adapter = new DeckAdapter(filteredDecks, this);
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