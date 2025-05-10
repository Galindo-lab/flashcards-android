package com.example.flashcards.activities;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.models.Deck;

public class DeckListActivity extends AppCompatActivity {


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

        // create adapter
        ListView listDecks = findViewById(R.id.deck_list);

        try (DBHelper dbHelper = new DBHelper(this)) {

            ArrayAdapter<Deck> listAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    dbHelper.getAllDecks()
            );

            listDecks.setAdapter(listAdapter);

        } catch (SQLiteException e) {
            Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT).show();
        }


    }
}