package com.example.flashcards.activities.deckList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcards.R;
import com.example.flashcards.models.Deck;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {
    private List<Deck> decks;
    private OnDeckClickListener listener;

    public interface OnDeckClickListener {
        void onDeckClick(Deck deck);
    }

    public DeckAdapter(List<Deck> decks, OnDeckClickListener listener) {
        this.decks = decks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deck_card, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        Deck deck = decks.get(position);
        holder.deckName.setText(deck.getName());
        holder.deckDescription.setText(deck.getDescription());
        holder.deckCategory.setText(deck.getCategory());

        holder.itemView.setOnClickListener(v -> listener.onDeckClick(deck));
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    public static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView deckName, deckDescription, deckCategory;
        ImageView favoriteIcon;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            deckName = itemView.findViewById(R.id.deck_name);
            deckDescription = itemView.findViewById(R.id.deck_description);
            deckCategory = itemView.findViewById(R.id.deck_category);
        }
    }
}
