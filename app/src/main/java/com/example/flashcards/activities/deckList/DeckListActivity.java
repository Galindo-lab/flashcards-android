package com.example.flashcards.activities.deckList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcards.DBHelper;
import com.example.flashcards.R;
import com.example.flashcards.activities.DeckDetailsActivity;
import com.example.flashcards.activities.cardList.CardListActivity;
import com.example.flashcards.models.Card;
import com.example.flashcards.models.Deck;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeckListActivity extends AppCompatActivity implements DeckAdapter.OnDeckClickListener {

    private Deck deck;
    private Spinner categorySpinner;
    private RecyclerView recyclerView;
    private DeckAdapter adapter;
    private List<Deck> allDecks;

    // Para manejar la cámara y permisos
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

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

        // Inicializar vistas
        categorySpinner = findViewById(R.id.category_spinner);
        recyclerView = findViewById(R.id.deck_recycler_view);
        recyclerView.setHasFixedSize(true);

        // Cargar decks y configurar spinner
        loadDecksAndCategories();
        initializeCameraLaunchers();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Recargar los decks cuando la actividad se reinicie
        loadDecksAndCategories();
    }

    private void initializeCameraLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && deck != null) {
                        // Simulamos la generación de tarjetas desde la foto
                        generateCardsFromPhoto();
                    }
                });

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                            openCamera();
                    } else {
                        Toast.makeText(this, "Se necesita permiso de la cámara", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }


    private void generateCardsFromPhoto() {
        // Esto es un demo - en una app real aquí procesarías la imagen con IA
        // Por ahora generamos tarjetas de ejemplo

        List<Card> demoCards = Arrays.asList(
                new Card(deck.getId(), "Concepto 1", "Definición generada desde foto", false),
                new Card(deck.getId(), "Término clave", "Explicación automática", false),
                new Card(deck.getId(), "Dato importante", "Información extraída de la imagen", false)
        );

        try (DBHelper dbHelper = new DBHelper(this)) {
            for (Card card : demoCards) {
                dbHelper.createCard(card);
            }
            Toast.makeText(this, "3 tarjetas generadas desde la foto", Toast.LENGTH_SHORT).show();

            // Mostrar diálogo de éxito
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Tarjetas generadas")
                    .setMessage("Se han creado 3 tarjetas basadas en la foto")
                    .setPositiveButton("Ver tarjetas", (dialog, which) -> navigateToCardList())
                    .setNegativeButton("Seguir editando", null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar tarjetas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCardList() {
        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra("deck_data", deck);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void loadDecksAndCategories() {
        try (DBHelper dbHelper = new DBHelper(this)) {
            // Obtener todos los decks
            allDecks = dbHelper.getAllDecks();

            // Obtener categorías únicas
            List<String> categories = new ArrayList<>();
            categories.add("Todas las categorías"); // Opción por defecto

            for (Deck deck : allDecks) {
                if (deck.getCategory() != null && !deck.getCategory().isEmpty()
                        && !categories.contains(deck.getCategory())) {
                    categories.add(deck.getCategory());
                }
            }

            // Configurar el spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, categories);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);

            // Mostrar todos los decks inicialmente
            adapter = new DeckAdapter(allDecks, this);
            recyclerView.setAdapter(adapter);

            // Listener para el spinner
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCategory = parent.getItemAtPosition(position).toString();
                    filterDecksByCategory(selectedCategory);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No hacer nada
                }
            });
        }
    }

    private void filterDecksByCategory(String category) {
        List<Deck> filteredDecks = new ArrayList<>();

        if (category.equals("Todas las categorías")) {
            filteredDecks.addAll(allDecks);
        } else {
            for (Deck deck : allDecks) {
                if (category.equals(deck.getCategory())) {
                    filteredDecks.add(deck);
                }
            }
        }

        adapter = new DeckAdapter(filteredDecks, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDeckClick(Deck deck) {
        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra("deck_data", deck);
        startActivity(intent);
    }

    public void floatingButtonOnClick(View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Crear nuevo deck")
                .setMessage("¿Deseas crear un deck con IA?")
                .setPositiveButton("Sí, con IA", (dialog, which) -> showImageSourceDialog())
                .setNegativeButton("No, manualmente", (dialog, which) -> {
                    Intent intent = new Intent(this, DeckDetailsActivity.class);
                    startActivity(intent);
                })
                .show();
    }

    private void showImageSourceDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Seleccionar fuente de imagen")
                .setItems(new String[]{"Tomar foto", "Elegir de galería"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Tomar foto
                            openCamera();
                            break;
                        case 1: // Elegir de galería
                            openGallery();
                            break;
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    // Declara estos launchers al inicio de la clase junto con los otros
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    createAIDemoDeck("Galería");
                }
            });

    private void createAIDemoDeck(String source) {
        try (DBHelper dbHelper = new DBHelper(this)) {
            Deck demoDeck = new Deck();
            demoDeck.setName("Demo desde " + source);
            demoDeck.setDescription("Deck generado desde " + (source.equals("Cámara") ? "una foto" : "la galería"));
            demoDeck.setCategory(source);

            long deckId = dbHelper.createDeck(demoDeck);

            String[] terms = {"Concepto 1", "Concepto 2", "Tema principal"};
            String[] definitions = {
                    "Información extraída de la " + source.toLowerCase(),
                    "Datos relevantes identificados",
                    "Análisis generado automáticamente"
            };

            for (int i = 0; i < terms.length; i++) {
                Card card = new Card((int) deckId, terms[i], definitions[i], false);
                dbHelper.createCard(card);
            }

            Toast.makeText(this, "Deck creado desde " + source, Toast.LENGTH_SHORT).show();
            loadDecksAndCategories();
        }
    }

    private void createAIDemoDeck() {
        try (DBHelper dbHelper = new DBHelper(this)) {
            // Crear un deck de prueba con IA
            Deck demoDeck = new Deck();
            demoDeck.setName("Demo generado por IA");
            demoDeck.setDescription("Este es un deck de demostración generado automáticamente");
            demoDeck.setCategory("IA");

            // Guardar el deck en la base de datos
            long deckId = dbHelper.createDeck(demoDeck);

            // Crear algunas tarjetas de prueba
            String[] terms = {"Machine Learning", "Neural Network", "Deep Learning", "Natural Language Processing"};
            String[] definitions = {
                    "Rama de la IA que permite a los sistemas aprender de datos",
                    "Sistema de algoritmos que imita el funcionamiento del cerebro humano",
                    "Subcampo del ML que utiliza redes neuronales con múltiples capas",
                    "Capacidad de una computadora para entender el lenguaje humano"
            };

            for (int i = 0; i < terms.length; i++) {
                Card card = new Card((int) deckId, terms[i], definitions[i], false);
                dbHelper.createCard(card);
            }

            Toast.makeText(this, "Deck generado por IA creado", Toast.LENGTH_SHORT).show();
            loadDecksAndCategories(); // Recargar la lista
        }
    }
}