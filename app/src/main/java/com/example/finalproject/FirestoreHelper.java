package com.example.finalproject;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreHelper {
    private final FirebaseFirestore db;
    private final CollectionReference notesRef;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        notesRef = db.collection("notes");
    }

    public void addNote(Note note) {
        notesRef.add(note);
    }

    public void getNotes(NotesCallback callback) {
        notesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Note> notes = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Note note = document.toObject(Note.class);
                    note.setId(document.getId());
                    notes.add(note);
                }
                callback.onCallback(notes);
            }
        });
    }

    public void updateNote(Note note) {
        notesRef.document(note.getId()).set(note);
    }

    public void deleteNote(String noteId) {
        notesRef.document(noteId).delete();
    }

    public interface NotesCallback {
        void onCallback(List<Note> notes);
    }
} 