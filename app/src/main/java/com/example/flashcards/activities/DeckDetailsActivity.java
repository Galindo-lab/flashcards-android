
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
    }

    public Deck getFormData() throws Exception {
        try {
            // Obtener referencias a los elementos del formulario
            TextInputEditText etDeckName = findViewById(R.id.etDeckName);
            TextInputEditText etDeckDescription = findViewById(R.id.etDeckDescription);
            TextInputEditText etDeckCategory = findViewById(R.id.etDeckCategory);
            SwitchCompat switchFavorite = findViewById(R.id.switchFavorite);

            // Validar que los campos existen
            if (etDeckName == null || etDeckDescription == null ||
                    etDeckCategory == null || switchFavorite == null) {
                throw new Exception("Error al acceder a los campos del formulario");
            }

            // Obtener y validar los valores
            String name = etDeckName.getText().toString().trim();
            if (name.isEmpty()) {
                throw new Exception("El nombre del mazo es obligatorio");
            }

            // Crear el objeto Deck
            Deck deck = new Deck();
            deck.setName(name);
            deck.setDescription(etDeckDescription.getText().toString().trim());
            deck.setCategory(etDeckCategory.getText().toString().trim());
            deck.setFavorite(switchFavorite.isChecked());

            return deck;

        } catch (Exception e) {
            // Relanzar la excepción para que el llamante la maneje
            throw new Exception(e.getMessage(), e);
        }
    }



    public void saveDeck(View view) {
        try {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            Deck deck = getFormData();

            if (dbHelper.createDeck(deck) == -1) {
                Toast.makeText(this, "Un Error ha ocurrido", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Mazo '" + deck.getName() + "' creado", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, CardListActivity.class);
            intent.putExtra("deck_data", deck);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}