package com.example.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private static final String NEWS_API_KEY = "YOUR_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotesAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddNote);
        fab.setOnClickListener(v -> showAddNoteDialog());

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logoutUser());

        // Setup News RecyclerView
        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter();
        newsRecyclerView.setAdapter(newsAdapter);

        loadNotes();
        loadNews();
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_note, null);
        
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText bodyInput = dialogView.findViewById(R.id.bodyInput);

        builder.setView(dialogView)
            .setTitle("Add New Note")
            .setPositiveButton("Add", (dialog, id) -> {
                String title = titleInput.getText().toString().trim();
                String body = bodyInput.getText().toString().trim();
                
                if (!title.isEmpty() && !body.isEmpty()) {
                    saveNote(title, body);
                }
            })
            .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void saveNote(String title, String body) {
        String userId = mAuth.getCurrentUser().getUid();
        
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("body", body);
        note.put("userId", userId);

        db.collection("notes")
            .add(note)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(MainActivity.this, 
                    "Note added successfully", Toast.LENGTH_SHORT).show();
                loadNotes();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, 
                    "Error adding note: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void loadNotes() {
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("notes")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Post> notes = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Post note = document.toObject(Post.class);
                    notes.add(note);
                }
                adapter.setNotes(notes);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, 
                    "Error loading notes: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void loadNews() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getTopHeadlines("us", NEWS_API_KEY)
            .enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<News> articles = response.body().getArticles();
                        if (articles != null && !articles.isEmpty()) {
                            newsAdapter.setNews(articles);
                            Log.d("NewsAPI", "Loaded " + articles.size() + " articles");
                        } else {
                            Log.e("NewsAPI", "No articles found");
                            Toast.makeText(MainActivity.this, 
                                "No news articles available", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            Log.e("NewsAPI", "Error: " + response.errorBody().string());
                            Toast.makeText(MainActivity.this, 
                                "Error loading news: " + response.code(), 
                                Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    Log.e("NewsAPI", "Network error: " + t.getMessage());
                    Toast.makeText(MainActivity.this, 
                        "Network error: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}