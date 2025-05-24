package com.example.flashcards;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.flashcards.models.Card;
import com.example.flashcards.models.Deck;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "card_database";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Sentencia para crear la tabla Decks
        String CREATE_TABLE_DECKS =
                "CREATE TABLE decks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "description TEXT," +
                        "is_favorite INTEGER DEFAULT 0," +
                        "category TEXT" + ")";

        db.execSQL(CREATE_TABLE_DECKS);

        // Sentencia para crear la tabla Tarjetas
        String CREATE_TABLE_CARDS =
                "CREATE TABLE cards (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "deck_id_fk INTEGER NOT NULL," +
                        "term TEXT NOT NULL," +
                        "definition TEXT NOT NULL," +
                        "is_favorite INTEGER DEFAULT 0," +
                        "FOREIGN KEY(deck_id_fk) REFERENCES decks(id) ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_TABLE_CARDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DBHelper", "Actualizando la base de datos de la versión " + oldVersion + " a la versión " + newVersion);
        // No se realizan cambios en la base de datos en la primera versión.
    }






    // Métodos para la gestión de Decks

    /**
     * Método para crear un nuevo Deck
     * @param deck
     * @return
     */
    public long createDeck(Deck deck) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", deck.getName());
        values.put("description", deck.getDescription());
        values.put("is_favorite", deck.isFavorite() ? 1 : 0);
        values.put("category", deck.getCategory());
        long id = db.insert("decks", null, values);
        db.close();
        Log.d("DBHelper", "Deck creado con id: " + id);
        return id;
    }

    /**
     * Método para obtener un Deck por su ID
     * @param id
     * @return
     */
    public Deck getDeck(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("decks",
                new String[]{"id", "name", "description", "is_favorite", "category"},
                "id=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Deck deck = new Deck();
        if(cursor != null && cursor.getCount() > 0) { //Verificamos que el cursor no sea nulo y que tenga al menos un registro.
            deck.setId(cursor.getInt(0));
            deck.setName(cursor.getString(1));
            deck.setDescription(cursor.getString(2));
            deck.setFavorite(cursor.getInt(3) > 0);
            deck.setCategory(cursor.getString(4));
            cursor.close();
        }
        db.close();
        return deck;
    }

    /**
     * Método para obtener todos los Decks
     * @return
     */
    public List<Deck> getAllDecks() {
        List<Deck> deckList = new ArrayList<>();
        String selectQuery = "SELECT * FROM decks";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Deck deck = new Deck();
                deck.setId(cursor.getInt(0));
                deck.setName(cursor.getString(1));
                deck.setDescription(cursor.getString(2));
                deck.setFavorite(cursor.getInt(3) > 0);
                deck.setCategory(cursor.getString(4));
                deckList.add(deck);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return deckList;
    }

    /**
     * Método para actualizar un Deck
     * @param deck
     * @return
     */
    public int updateDeck(Deck deck) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", deck.getName());
        values.put("description", deck.getDescription());
        values.put("is_favorite", deck.isFavorite() ? 1 : 0);
        values.put("category", deck.getCategory());
        int rowsAffected = db.update("decks", values, "id=?",
                new String[]{String.valueOf(deck.getId())});
        db.close();
        Log.d("DBHelper", "Deck actualizado, filas afectadas: " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Método para eliminar un Deck
     * @param id
     */
    public void deleteDeck(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("decks", "id=?",
                new String[]{String.valueOf(id)});
        db.close();
        Log.d("DBHelper", "Deck eliminado con id: " + id);
    }

    /**
     * Método para marcar un Deck como favorito
     * @param id
     * @param isFavorite
     */
    public void markDeckAsFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_favorite", isFavorite ? 1 : 0);
        int rowsAffected = db.update("decks", values, "id=?",
                new String[]{String.valueOf(id)});
        db.close();
        Log.d("DBHelper", "Deck marcado como favorito. Id: " + id + ", isFavorite: " + isFavorite + ". Filas afectadas: " + rowsAffected);
    }


    // Métodos para la gestión de Tarjetas

    // Método para crear una nueva Tarjeta
    public long createCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deck_id_fk", card.getDeckId());
        values.put("term", card.getTerm());
        values.put("definition", card.getDefinition());
        values.put("is_favorite", card.isFavorite() ? 1 : 0);
        long id = db.insert("cards", null, values);
        db.close();
        Log.d("DBHelper", "Tarjeta creada con id: " + id);
        return id;
    }

    // Método para obtener una Tarjeta por su ID
    public Card getCard(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cards",
                new String[]{"id", "deck_id_fk", "term", "definition", "is_favorite"},
                "id=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Card card = new Card(); // Inicializa card
        if(cursor != null && cursor.getCount() > 0) {
            card = new Card(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4) > 0);
            card.setId(cursor.getInt(0));
            cursor.close();
        }
        db.close();
        return card;
    }

    // Método para obtener todas las Tarjetas de un Deck
    public List<Card> getAllCardsInDeck(int deckId) {
        List<Card> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cards",
                new String[]{"id", "deck_id_fk", "term", "definition", "is_favorite"},
                "deck_id_fk=?",
                new String[]{String.valueOf(deckId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setId(cursor.getInt(0));
                card.setDeckId(cursor.getInt(1));
                card.setTerm(cursor.getString(2));
                card.setDefinition(cursor.getString(3));
                card.setFavorite(cursor.getInt(4) > 0);
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cardList;
    }

    // Método para actualizar una Tarjeta
    public int updateCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deck_id_fk", card.getDeckId());
        values.put("term", card.getTerm());
        values.put("definition", card.getDefinition());
        values.put("is_favorite", card.isFavorite() ? 1 : 0);
        int rowsAffected = db.update("cards", values, "id=?",
                new String[]{String.valueOf(card.getId())});
        db.close();
        Log.d("DBHelper", "Tarjeta actualizada, filas afectadas: " + rowsAffected);
        return rowsAffected;
    }

    // Método para eliminar una Tarjeta
    public void deleteCard(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cards", "id=?",
                new String[]{String.valueOf(id)});
        db.close();
        Log.d("DBHelper", "Tarjeta eliminada con id: " + id);
    }

    // Método para marcar una Tarjeta como favorita
    public void markCardAsFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_favorite", isFavorite ? 1 : 0);
        int rowsAffected = db.update("cards", values, "id=?",
                new String[]{String.valueOf(id)});
        db.close();
        Log.d("DBHelper", "Tarjeta marcada como favorita. Id: " + id + ", isFavorite: " + isFavorite + ". Filas afectadas: " + rowsAffected);
    }
    // Método para obtener todas las tarjetas favoritas
    public List<Card> getFavoriteCards() {
        List<Card> favoriteCards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cards",
                new String[]{"id", "deck_id_fk", "term", "definition", "is_favorite"},
                "is_favorite=?",
                new String[]{"1"}, null, null, null); // 1 representa verdadero en la base de datos

        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setId(cursor.getInt(0));
                card.setDeckId(cursor.getInt(1));
                card.setTerm(cursor.getString(2));
                card.setDefinition(cursor.getString(3));
                card.setFavorite(cursor.getInt(4) > 0);
                favoriteCards.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteCards;
    }

    // Método para obtener todos los decks favoritos
    public List<Deck> getFavoriteDecks() {
        List<Deck> favoriteDecks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("decks",
                new String[]{"id", "name", "description", "is_favorite", "category"},
                "is_favorite=?",
                new String[]{"1"}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Deck deck = new Deck();
                deck.setId(cursor.getInt(0));
                deck.setName(cursor.getString(1));
                deck.setDescription(cursor.getString(2));
                deck.setFavorite(cursor.getInt(3) > 0);
                deck.setCategory(cursor.getString(4));
                favoriteDecks.add(deck);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteDecks;
    }

    // Método para obtener decks por categoría
    public List<Deck> getDecksByCategory(String category) {
        List<Deck> decksInCategory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("decks",
                new String[]{"id", "name", "description", "is_favorite", "category"},
                "category=?",
                new String[]{category}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Deck deck = new Deck();
                deck.setId(cursor.getInt(0));
                deck.setName(cursor.getString(1));
                deck.setDescription(cursor.getString(2));
                deck.setFavorite(cursor.getInt(3) > 0);
                deck.setCategory(cursor.getString(4));
                decksInCategory.add(deck);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return decksInCategory;
    }


    // En DBHelper.java, agrega este método en la sección de métodos públicos:

    /**
     * Método para borrar TODOS los datos de la base de datos (SOLO PARA PRUEBAS)
     */
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Eliminar todas las tarjetas
            db.delete("cards", null, null);
            // Eliminar todos los decks
            db.delete("decks", null, null);
            Log.d("DBHelper", "Todos los datos han sido eliminados");
        } catch (Exception e) {
            Log.e("DBHelper", "Error al eliminar todos los datos", e);
        } finally {
            db.close();
        }
    }
}
