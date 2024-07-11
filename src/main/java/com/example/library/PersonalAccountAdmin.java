package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.library.adapters.AdapterAppAdmin;
import com.example.library.adapters.AdapterTicketAdmin;
import com.example.library.db.Application;
import com.example.library.db.FirebaseDatabaseManager;
import com.example.library.db.Ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PersonalAccountAdmin extends AppCompatActivity {
    private RecyclerView listApplication;
    private RecyclerView listTicket;
    private AdapterAppAdmin adapterApplication;
    private AdapterTicketAdmin adapterTicket;
    private TextView textViewStudentName;
    private final FirebaseDatabaseManager databaseManager = FirebaseDatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account_admin);

        init();
        displayFullName();
        fetchUserApplications();
    }

    private void init() {
        listApplication = findViewById(R.id.recyclerApplications);
        adapterApplication = new AdapterAppAdmin(this, databaseManager);
        listApplication.setLayoutManager(new LinearLayoutManager(this));
        listApplication.setAdapter(adapterApplication);

        listTicket = findViewById(R.id.recyclerBooks);
        adapterTicket = new AdapterTicketAdmin(this, databaseManager);
        listTicket.setLayoutManager(new LinearLayoutManager(this));
        listTicket.setAdapter(adapterTicket);

        textViewStudentName = findViewById(R.id.textViewStudentName);
    }

    private void displayFullName() {
        String fullName = getIntent().getStringExtra("fullName");
        if (fullName != null) {
            textViewStudentName.setText(fullName);
        }
    }

    private void fetchUserApplications() {
        String fullName = getIntent().getStringExtra("fullName");

        if (fullName != null) {
            // Разделение полного имени на фамилию, имя и отчество
            String[] parts = fullName.split("\\s+");
            if (parts.length >= 3) { // Проверка наличия всех частей имени
                String surname = parts[0];
                String name = parts[1];
                String patronymic = parts[2];

                adapterApplication.setDataWithFullName(surname, name, patronymic);

                // Получение ссылки на базу данных пользователей
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

                // Запрос к базе данных для поиска пользователя по его имени, фамилии и отчеству
                Query query = usersRef.orderByChild("surname").equalTo(surname);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Application> userApplications = new ArrayList<>();
                        List<Ticket> userTickets = new ArrayList<>();

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            // Получаем данные пользователя
                            String nameFromDB = userSnapshot.child("name").getValue(String.class);
                            String patronymicFromDB = userSnapshot.child("patronymic").getValue(String.class);

                            // Проверяем имя и отчество пользователя
                            if (nameFromDB.equals(name) && patronymicFromDB.equals(patronymic)) {
                                String userId = userSnapshot.getKey();
                                // Получаем приложения пользователя
                                DataSnapshot applicationsSnapshot = userSnapshot.child("applications");
                                for (DataSnapshot applicationSnapshot : applicationsSnapshot.getChildren()) {
                                    Application application = applicationSnapshot.getValue(Application.class);
                                    userApplications.add(application);
                                }

                                // Получаем билеты пользователя
                                DataSnapshot ticketsSnapshot = userSnapshot.child("ticket");
                                for (DataSnapshot ticketSnapshot : ticketsSnapshot.getChildren()) {
                                    String ticketKey = ticketSnapshot.getKey(); // Получаем уникальный ключ записи
                                    Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                                    if (ticketKey != null && ticket != null) {
                                        ticket.setId(ticketKey); // Устанавливаем уникальный ключ в объект Ticket
                                        userTickets.add(ticket);
                                    }
                                }

                                adapterTicket.setData(userTickets, userId);
                            }
                        }

                        // Устанавливаем данные в адаптеры
                        adapterApplication.setData(userApplications);

                        // Обновляем адаптеры
                        adapterApplication.notifyDataSetChanged();
                        adapterTicket.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Обработка ошибки
                    }
                });
            }
        }
    }
}