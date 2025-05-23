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
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.activities.CreateCardActivity;
import com.example.flashcards.activities.DeckDetailsActivity;
import com.example.flashcards.activities.ViewCardsActivity;
import com.example.flashcards.activities.deckList.DeckAdapter;
import com.example.flashcards.activities.deckList.DeckListActivity;
import com.example.flashcards.models.Card;
import com.example.flashcards.models.Deck;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CardListActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {

    private Deck deck;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;


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

        // cargar datos
        Intent intent = getIntent();
        this.deck = (Deck) intent.getSerializableExtra("deck_data");


        // cargar barra
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Para el botón de retroceso
        }

        toolbar.setTitle(this.deck.getName());

        loadCards();
    }

    private void loadCards() {
        RecyclerView recyclerView;
        List<Card> cards;

        try (DBHelper dbHelper = new DBHelper(this)) {
            recyclerView = findViewById(R.id.card_recycler_view);
            recyclerView.setHasFixedSize(true);
            cards = dbHelper.getAllCardsInDeck(this.deck.getId());
        }

        CardAdapter adapter = new CardAdapter(cards, this);
        recyclerView.setAdapter(adapter);
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
            // cargar los datos para editar el deck
            Intent intent = new Intent(this, DeckDetailsActivity.class);
            intent.putExtra("deck_data", deck);
            startActivity(intent);

            return true;
        } else if (id == R.id.action_delete) {
            // dialog para eliminar tajeta
            ShowDeleteConfiramtionDialog();
            return true;
        } else if (id == R.id.action_practice) {
            Intent intent = new Intent(this, ViewCardsActivity.class);
            intent.putExtra("deck_id", deck.getId()); // deckId es el ID del deck que quieres ver
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDeck() {
        try (DBHelper dbHelper = new DBHelper(this)) {
            dbHelper.deleteDeck(this.deck.getId());
        }

        Toast.makeText(this, "Mazo '" + this.deck.getName() + "' Eliminado", Toast.LENGTH_LONG).show();

        // Configura el Intent para limpiar la pila de actividades
        Intent intent = new Intent(this, DeckListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish(); // Cierra la actividad actual
    }

    private void ShowDeleteConfiramtionDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este mazo?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteDeck())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void floatingButtonOnClick(View view) {
        Intent intent = new Intent(this, CreateCardActivity.class);
        intent.putExtra("deck_id", deck.getId());

        startActivity(intent);
    }

    @Override
    public void onCardClick(Card card) {
        Intent intent = new Intent(this, CreateCardActivity.class);
        intent.putExtra("card_data", card);

        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Recargar los decks cuando la actividad se reinicie
        loadCards();
    }
}