package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.library.adapters.AdapterAppAdmin;
import com.example.library.adapters.AdapterTicketAdmin;
import com.example.library.db.Application;
import com.example.library.db.FirebaseDatabaseManager;
import com.example.library.fragments.TicketAdmin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AppListAdmin extends AppCompatActivity {
    private RecyclerView listApplication;
    private AdapterAppAdmin adapterApplication;
    private final FirebaseDatabaseManager databaseManager = FirebaseDatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_admin);

        init();
        fetchUserApplications();
    }

    private void init() {
        listApplication = findViewById(R.id.listApp);
        adapterApplication = new AdapterAppAdmin(this, databaseManager);
        listApplication.setLayoutManager(new LinearLayoutManager(this));
        listApplication.setAdapter(adapterApplication);
    }

    private void fetchUserApplications() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        Query query = usersRef.orderByChild("admin").equalTo(false);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Application> userApplications = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Получаем заявки пользователя
                    DataSnapshot applicationsSnapshot = userSnapshot.child("applications");
                    for (DataSnapshot applicationSnapshot : applicationsSnapshot.getChildren()) {
                        Application application = applicationSnapshot.getValue(Application.class);
                        userApplications.add(application);
                    }
                }

                // Ваши действия с данными, например, установка данных в адаптер
                adapterApplication.setData(userApplications);
                adapterApplication.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки
            }
        });
    }

}