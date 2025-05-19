package com.example.flashcards.activities;

import android.os.Bundle;
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

public class CreateCardActivity extends AppCompatActivity {

    private TextInputEditText etCardTerm, etCardDefinition;
    private Button btnSaveCard;
    private DBHelper dbHelper;
    private int deckId;

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



        // Inicializar vistas
        etCardTerm = findViewById(R.id.etCardTerm);
        etCardDefinition = findViewById(R.id.etCardDefinition);
        btnSaveCard = findViewById(R.id.btnSaveCard);


        // Configurar la toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    private void saveCard() {
        String term = etCardTerm.getText().toString().trim();
        String definition = etCardDefinition.getText().toString().trim();

        // Validar los campos
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

        // Crear la nueva carta
        boolean isFavorite = ((androidx.appcompat.widget.SwitchCompat) findViewById(R.id.switchFavorite)).isChecked();
        Card newCard = new Card(deckId, term, definition, isFavorite);
        long cardId = dbHelper.createCard(newCard);

        if (cardId != -1) {
            Toast.makeText(this, "Carta creada exitosamente", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar la actividad después de guardar
        } else {
            Toast.makeText(this, "Error al crear la carta", Toast.LENGTH_SHORT).show();
        }
    }

}