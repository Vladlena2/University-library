package com.example.library.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseManager {
    private static FirebaseDatabaseManager instance;
    private DatabaseReference databaseRef;

    private FirebaseDatabaseManager() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized FirebaseDatabaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseDatabaseManager();
        }
        return instance;
    }

    public void writeDataToDatabase(String node, Object data) {
        DatabaseReference nodeRef = databaseRef.child(node);
        String key = nodeRef.push().getKey();
        if (key != null) {
            nodeRef.child(key).setValue(data);
        }
    }

    public void readDataFromDatabase(String node, ValueEventListener listener) {
        // Для узла базы данных, указанного в параметре node. Этот метод добавляет слушатель к одноразовому чтению данных из указанного узла
        databaseRef.child(node).addListenerForSingleValueEvent(listener);
    }

}

