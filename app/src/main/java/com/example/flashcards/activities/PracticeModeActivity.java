package com.example.flashcards.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.models.Card;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class PracticeModeActivity extends AppCompatActivity {

    private TextView tvDefinition, tvResult, tvCardCounter, tvFinalScore;
    private TextInputEditText etTerm;
    private Button btnCheckAnswer, btnNext, btnShareResults;
    private LinearLayout practiceLayout, resultsLayout;
    private List<Card> cardList;
    private List<Boolean> answersResults;
    private int currentPosition = 0;
    private int deckId;
    private String deckName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode);

        // Initialize views
        tvDefinition = findViewById(R.id.tvDefinition);
        tvResult = findViewById(R.id.tvResult);
        tvCardCounter = findViewById(R.id.tvCardCounter);
        etTerm = findViewById(R.id.etTerm);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnNext = findViewById(R.id.btnNext);
        practiceLayout = findViewById(R.id.practiceLayout);
        resultsLayout = findViewById(R.id.resultsLayout);
        tvFinalScore = findViewById(R.id.tvFinalScore);
        btnShareResults = findViewById(R.id.btnShareResults);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize results list
        answersResults = new ArrayList<>();

        // Get deck ID from intent
        deckId = getIntent().getIntExtra("deck_id", -1);
        if (deckId == -1) {
            Toast.makeText(this, "Error: No deck ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load cards from deck
        DBHelper dbHelper = new DBHelper(this);
        cardList = dbHelper.getAllCardsInDeck(deckId);
        deckName = dbHelper.getDeck(deckId).getName();

        if (cardList.isEmpty()) {
            Toast.makeText(this, "This deck has no cards", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hide results layout initially
        resultsLayout.setVisibility(View.GONE);
        practiceLayout.setVisibility(View.VISIBLE);

        // Show first card
        showCard(currentPosition);

        // Set up button listeners
        btnCheckAnswer.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> showNextCard());
        btnShareResults.setOnClickListener(v -> shareResults());
    }

    private void showCard(int position) {
        if (position < 0 || position >= cardList.size()) {
            return;
        }

        Card currentCard = cardList.get(position);
        tvDefinition.setText(currentCard.getDefinition());
        etTerm.setText("");
        tvResult.setVisibility(View.GONE);
        btnNext.setEnabled(false);

        // Update counter
        tvCardCounter.setText(String.format("%d/%d", position + 1, cardList.size()));
    }

    private void checkAnswer() {
        Card currentCard = cardList.get(currentPosition);
        String userAnswer = etTerm.getText().toString().trim();
        String correctAnswer = currentCard.getTerm();

        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Please enter your answer", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
        answersResults.add(isCorrect);

        tvResult.setVisibility(View.VISIBLE);

        if (isCorrect) {
            tvResult.setText("¡Correcto!");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvResult.setText(String.format("Incorrecto. La respuesta correcta es: %s", correctAnswer));
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        btnNext.setEnabled(true);
        btnCheckAnswer.setEnabled(false);
    }

    private void showNextCard() {
        if (currentPosition < cardList.size() - 1) {
            currentPosition++;
            showCard(currentPosition);
            btnCheckAnswer.setEnabled(true);
        } else {
            showFinalResults();
        }
    }

    private void showFinalResults() {
        practiceLayout.setVisibility(View.GONE);
        resultsLayout.setVisibility(View.VISIBLE);

        int correctAnswers = 0;
        for (boolean result : answersResults) {
            if (result) correctAnswers++;
        }

        int totalQuestions = cardList.size();
        int percentage = (int) (((float) correctAnswers / totalQuestions) * 100);

        String resultsText = String.format(
                "Resultados de %s:\n\n" +
                        "Correctas: %d\n" +
                        "Incorrectas: %d\n" +
                        "Porcentaje: %d%%",
                deckName, correctAnswers, totalQuestions - correctAnswers, percentage
        );

        tvFinalScore.setText(resultsText);
    }

    private void shareResults() {
        int correctAnswers = 0;
        for (boolean result : answersResults) {
            if (result) correctAnswers++;
        }

        int totalQuestions = cardList.size();
        int percentage = (int) (((float) correctAnswers / totalQuestions) * 100);

        String shareText = String.format(
                "¡Acabo de practicar con %s y obtuve %d de %d correctas (%d%%)! " +
                        "¿Quieres probar esta app de flashcards?",
                deckName, correctAnswers, totalQuestions, percentage
        );

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", shareText);
        startActivity(sendIntent);
    }
}