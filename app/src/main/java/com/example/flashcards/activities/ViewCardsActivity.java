package com.example.flashcards.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.List;

public class ViewCardsActivity extends AppCompatActivity {

    private TextView tvTerm, tvDefinition, tvCardCounter;
    private Button btnShowAnswer, btnPrevious, btnNext;
    private List<Card> cardList;
    private int currentPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_cards);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener el ID del deck del intent
        int deckId = getIntent().getIntExtra("deck_id", -1);
        if (deckId == -1) {
            Toast.makeText(this, "Error: No se proporcionó el ID del deck", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        tvTerm = findViewById(R.id.tvTerm);
        tvDefinition = findViewById(R.id.tvDefinition);
        tvCardCounter = findViewById(R.id.tvCardCounter);
        btnShowAnswer = findViewById(R.id.btnShowAnswer);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        // Configurar toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Cargar las tarjetas del deck
        DBHelper dbHelper = new DBHelper(this);
        cardList = dbHelper.getAllCardsInDeck(deckId);

        if (cardList.isEmpty()) {
            Toast.makeText(this, "Este deck no tiene tarjetas", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mostrar la primera tarjeta
        showCard(currentPosition);

        // Configurar listeners
        btnShowAnswer.setOnClickListener(v -> toggleAnswerVisibility());
        btnPrevious.setOnClickListener(v -> showPreviousCard());
        btnNext.setOnClickListener(v -> showNextCard());


    }


    private void showCard(int position) {
        if (position < 0 || position >= cardList.size()) {
            return;
        }

        Card currentCard = cardList.get(position);
        tvTerm.setText(currentCard.getTerm());
        tvDefinition.setText(currentCard.getDefinition());
        tvDefinition.setVisibility(View.GONE);
        btnShowAnswer.setText("Mostrar Respuesta");

        // Actualizar contador
        tvCardCounter.setText(String.format("%d/%d", position + 1, cardList.size()));

        // Manejar estado de los botones de navegación
        btnPrevious.setEnabled(position > 0);
        btnNext.setEnabled(position < cardList.size() - 1);
    }

    private void toggleAnswerVisibility() {
        if (tvDefinition.getVisibility() == View.VISIBLE) {
            tvDefinition.setVisibility(View.GONE);
            btnShowAnswer.setText("Mostrar Respuesta");
        } else {
            tvDefinition.setVisibility(View.VISIBLE);
            btnShowAnswer.setText("Ocultar Respuesta");
        }
    }

    private void showPreviousCard() {
        if (currentPosition > 0) {
            currentPosition--;
            showCard(currentPosition);
        }
    }

    private void showNextCard() {
        if (currentPosition < cardList.size() - 1) {
            currentPosition++;
            showCard(currentPosition);
        }
    }
}