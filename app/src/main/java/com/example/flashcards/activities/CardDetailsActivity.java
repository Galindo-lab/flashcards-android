package com.example.flashcards.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.models.Card;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.widget.SwitchCompat;

public class CardDetailsActivity extends AppCompatActivity {

    private TextInputEditText etCardTerm, etCardDefinition;
    private Button btnSaveCard, btnDeleteCard;
    private SwitchCompat switchFavorite;
    private DBHelper dbHelper;
    private int deckId; // for creating new card
    private Card card; // for editing existing card
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_card);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etCardTerm = findViewById(R.id.etCardTerm);
        etCardDefinition = findViewById(R.id.etCardDefinition);
        btnSaveCard = findViewById(R.id.btnSaveCard);
        btnDeleteCard = findViewById(R.id.btnDeleteCard);
        switchFavorite = findViewById(R.id.switchFavorite);
        dbHelper = new DBHelper(this);

        loadData();

        // Configure toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Set click listener for save button
        btnSaveCard.setOnClickListener(this::saveCard);
    }

    public void loadData() {
        // Check if we're editing an existing card
        card = (Card) getIntent().getSerializableExtra("card_data");
        if (card != null) {
            isEditMode = true;
            // Populate fields with card data
            etCardTerm.setText(card.getTerm());
            etCardDefinition.setText(card.getDefinition());
            switchFavorite.setChecked(card.isFavorite());
            // Update button text
            btnSaveCard.setText("Actualizar Carta");
            // Show delete button
            btnDeleteCard.setVisibility(View.VISIBLE);
            // Update toolbar title
            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Editar Carta");
        } else {
            // We're creating a new card, get deck ID
            deckId = getIntent().getIntExtra("deck_id", -1);
            if (deckId == -1) {
                Toast.makeText(this, "Error: No se proporcionó el ID del deck", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void saveCard(View view) {
        String term = etCardTerm.getText().toString().trim();
        String definition = etCardDefinition.getText().toString().trim();

        // Validate fields
        if (term.isEmpty()) {
            etCardTerm.setError("El término es requerido");
            etCardTerm.requestFocus();
            return;
        }

        if (definition.isEmpty()) {
            etCardDefinition.setError("La definición es requerida");
            etCardDefinition.requestFocus();
            return;
        }

        boolean isFavorite = switchFavorite.isChecked();

        if (isEditMode) {
            // Update existing card
            card.setTerm(term);
            card.setDefinition(definition);
            card.setFavorite(isFavorite);

            int rowsAffected = dbHelper.updateCard(card);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Carta actualizada exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar la carta", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Create new card
            Card newCard = new Card(deckId, term, definition, isFavorite);
            long cardId = dbHelper.createCard(newCard);

            if (cardId != -1) {
                Toast.makeText(this, "Carta creada exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al crear la carta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteCard(View view) {
        if (isEditMode && card != null) {
            dbHelper.deleteCard(card.getId());
            Toast.makeText(this, "Carta eliminada exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar la carta", Toast.LENGTH_SHORT).show();
        }
    }
}