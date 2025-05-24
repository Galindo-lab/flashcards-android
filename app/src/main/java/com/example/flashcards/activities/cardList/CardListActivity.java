package com.example.flashcards.activities.cardList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.activities.CardDetailsActivity;
import com.example.flashcards.activities.DeckDetailsActivity;
import com.example.flashcards.activities.PracticeModeActivity;
import com.example.flashcards.activities.QuizModeActivity;
import com.example.flashcards.activities.ViewCardsActivity;
import com.example.flashcards.activities.deckList.DeckListActivity;
import com.example.flashcards.fragments.DeckListFragment;
import com.example.flashcards.models.Card;
import com.example.flashcards.models.Deck;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CardListActivity extends AppCompatActivity implements
        CardAdapter.OnCardClickListener,
        DeckListFragment.OnDeckSelectedListener {

    private Deck deck;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_card_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Verificar si estamos en modo landscape
        isLandscape = findViewById(R.id.deck_list_container) != null;

        // Cargar datos
        Intent intent = getIntent();
        this.deck = (Deck) intent.getSerializableExtra("deck_data");

        // Configurar toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle(this.deck.getName());

        // Configurar el modo landscape si es necesario
        if (isLandscape) {
            setupLandscapeMode();
        }

        loadCards();
    }

    private void setupLandscapeMode() {
        // Agregar el fragmento de lista de decks
        DeckListFragment deckListFragment = new DeckListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.deck_list_container, deckListFragment);
        transaction.commit();
    }

    private void loadCards() {
        recyclerView = findViewById(R.id.card_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try (DBHelper dbHelper = new DBHelper(this)) {
            List<Card> cards = dbHelper.getAllCardsInDeck(this.deck.getId());
            cardAdapter = new CardAdapter(cards, this);
            recyclerView.setAdapter(cardAdapter);
        }
    }

    @Override
    public void onDeckSelected(int deckId) {
        // Actualizar el deck cuando se selecciona uno nuevo en landscape
        try (DBHelper dbHelper = new DBHelper(this)) {
            this.deck = dbHelper.getDeck(deckId);
            getSupportActionBar().setTitle(this.deck.getName());
            loadCards();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(this, DeckDetailsActivity.class);
            intent.putExtra("deck_data", deck);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        } else if (id == R.id.action_practice) {
            Intent intent = new Intent(this, ViewCardsActivity.class);
            intent.putExtra("deck_id", deck.getId());
            startActivity(intent);
            return true;
        } else if (id == R.id.action_practice2) {
            Intent intent = new Intent(this, PracticeModeActivity.class);
            intent.putExtra("deck_id", deck.getId());
            startActivity(intent);
            return true;
        } else if (id == R.id.action_practice3) {
            try (DBHelper dbHelper = new DBHelper(this)) {
                int cardCount = dbHelper.getAllCardsInDeck(this.deck.getId()).size();
                if (cardCount >= 3) {
                    Intent intent = new Intent(this, QuizModeActivity.class);
                    intent.putExtra("deck_id", this.deck.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(this,
                            "El deck necesita al menos 3 tarjetas para el modo Quiz",
                            Toast.LENGTH_LONG).show();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDeck() {
        try (DBHelper dbHelper = new DBHelper(this)) {
            dbHelper.deleteDeck(this.deck.getId());
        }

        Toast.makeText(this, "Mazo '" + this.deck.getName() + "' Eliminado", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, DeckListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este mazo?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteDeck())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void floatingButtonOnClick(View view) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra("deck_id", deck.getId());
        startActivity(intent);
    }

    @Override
    public void onCardClick(Card card) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra("card_data", card);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCards();

        // Actualizar el deck por si hubo cambios
        try (DBHelper dbHelper = new DBHelper(this)) {
            this.deck = dbHelper.getDeck(deck.getId());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(this.deck.getName());
            }
        }
    }
}