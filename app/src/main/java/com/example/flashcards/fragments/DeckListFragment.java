package com.example.flashcards.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcards.R;
import com.example.flashcards.activities.deckList.DeckAdapter;
import com.example.flashcards.DBHelper;
import com.example.flashcards.models.Deck;

import java.util.List;

public class DeckListFragment extends Fragment {

    private DeckAdapter adapter;
    private OnDeckSelectedListener listener;

    public interface OnDeckSelectedListener {
        void onDeckSelected(int deckId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDeckSelectedListener) {
            listener = (OnDeckSelectedListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck_list, container, false);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.deck_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DBHelper dbHelper = new DBHelper(getContext());
        List<Deck> decks = dbHelper.getAllDecks();

        adapter = new DeckAdapter(decks, deck -> {
            if (listener != null) {
                listener.onDeckSelected(deck.getId());
            }
        });

        recyclerView.setAdapter(adapter);
    }
}