package com.example.flashcards.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.models.Card;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizModeActivity extends AppCompatActivity {

    private TextView tvDefinition, tvQuestionCounter, tvQuizResults;
    private Button btnOption1, btnOption2, btnOption3, btnNextQuestion;
    private Button btnShareQuizResults, btnRestartQuiz;
    private LinearLayout quizLayout, resultsLayout;
    private List<Card> cardList;
    private List<Card> quizQuestions;
    private int currentQuestion = 0;
    private int correctAnswers = 0;
    private int deckId;
    private String deckName;
    private Card currentCard;
    private boolean answerSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_mode);

        // Initialize views
        tvDefinition = findViewById(R.id.tvDefinition);
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        quizLayout = findViewById(R.id.quizLayout);
        resultsLayout = findViewById(R.id.resultsLayout);
        tvQuizResults = findViewById(R.id.tvQuizResults);
        btnShareQuizResults = findViewById(R.id.btnShareQuizResults);
        btnRestartQuiz = findViewById(R.id.btnRestartQuiz);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

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

        if (cardList.size() < 3) {
            Toast.makeText(this, "Se necesitan al menos 3 tarjetas para el modo quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Prepare quiz questions (max 10)
        quizQuestions = new ArrayList<>(cardList);
        Collections.shuffle(quizQuestions);
        if (quizQuestions.size() > 10) {
            quizQuestions = quizQuestions.subList(0, 10);
        }

        // Hide results layout initially
        resultsLayout.setVisibility(View.GONE);
        quizLayout.setVisibility(View.VISIBLE);

        // Set up button listeners
        btnOption1.setOnClickListener(v -> checkAnswer(btnOption1));
        btnOption2.setOnClickListener(v -> checkAnswer(btnOption2));
        btnOption3.setOnClickListener(v -> checkAnswer(btnOption3));
        btnNextQuestion.setOnClickListener(v -> showNextQuestion());
        btnShareQuizResults.setOnClickListener(v -> shareQuizResults());
        btnRestartQuiz.setOnClickListener(v -> restartQuiz());

        // Show first question
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestion >= quizQuestions.size()) {
            showResults();
            return;
        }

        answerSelected = false;
        currentCard = quizQuestions.get(currentQuestion);
        tvDefinition.setText(currentCard.getDefinition());
        tvQuestionCounter.setText(String.format("Pregunta %d/%d", currentQuestion + 1, quizQuestions.size()));

        // Prepare options (1 correct + 2 incorrect)
        List<String> options = new ArrayList<>();
        options.add(currentCard.getTerm()); // Correct answer

        // Get 2 random incorrect answers
        List<Card> tempCards = new ArrayList<>(cardList);
        tempCards.remove(currentCard); // Remove correct answer
        Collections.shuffle(tempCards);

        for (int i = 0; i < 2 && i < tempCards.size(); i++) {
            options.add(tempCards.get(i).getTerm());
        }

        // Shuffle options
        Collections.shuffle(options);

        // Set options to buttons
        btnOption1.setText(options.get(0));
        btnOption2.setText(options.get(1));
        btnOption3.setText(options.get(2));

        // Reset buttons
        btnOption1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnOption2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnOption3.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnNextQuestion.setVisibility(View.GONE);
    }

    private void checkAnswer(Button selectedButton) {
        if (answerSelected) return;
        answerSelected = true;

        String selectedAnswer = selectedButton.getText().toString();
        boolean isCorrect = selectedAnswer.equals(currentCard.getTerm());

        // Highlight correct answer
        if (btnOption1.getText().toString().equals(currentCard.getTerm())) {
            btnOption1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (btnOption2.getText().toString().equals(currentCard.getTerm())) {
            btnOption2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            btnOption3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }

        // Highlight selected answer if wrong
        if (!isCorrect) {
            selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            correctAnswers++;
        }

        btnNextQuestion.setVisibility(View.VISIBLE);
    }

    private void showNextQuestion() {
        currentQuestion++;
        showQuestion();
    }

    private void showResults() {
        quizLayout.setVisibility(View.GONE);
        resultsLayout.setVisibility(View.VISIBLE);

        int percentage = (int) (((float) correctAnswers / quizQuestions.size()) * 100);
        String resultsText = String.format(
                "Resultados de %s:\n\n" +
                        "Correctas: %d/%d\n" +
                        "Porcentaje: %d%%",
                deckName, correctAnswers, quizQuestions.size(), percentage
        );

        tvQuizResults.setText(resultsText);
    }

    private void shareQuizResults() {
        int percentage = (int) (((float) correctAnswers / quizQuestions.size()) * 100);
        String shareText = String.format(
                "¡Acabo de completar el quiz de %s y obtuve %d de %d correctas (%d%%)! " +
                        "¿Quieres probar esta app de flashcards?",
                deckName, correctAnswers, quizQuestions.size(), percentage
        );

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", shareText);
        startActivity(sendIntent);
    }

    private void restartQuiz() {
        currentQuestion = 0;
        correctAnswers = 0;
        Collections.shuffle(quizQuestions);
        resultsLayout.setVisibility(View.GONE);
        quizLayout.setVisibility(View.VISIBLE);
        showQuestion();
    }
}