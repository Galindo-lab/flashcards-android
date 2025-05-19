package com.example.flashcards.activities.cardList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashcards.R;
import com.example.flashcards.models.Card;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<Card> cards;
    private OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(Card card);
    }

    public CardAdapter(List<Card> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.bind(card);

        holder.itemView.setOnClickListener(v -> listener.onCardClick(card));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardTerm;
        private final TextView cardDefinition;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTerm = itemView.findViewById(R.id.card_term);
            cardDefinition = itemView.findViewById(R.id.card_definition);
        }

        public void bind(Card card) {
            cardTerm.setText(card.getTerm());
            cardDefinition.setText(card.getDefinition());
        }
    }
}