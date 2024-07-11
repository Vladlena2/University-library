package com.example.library.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.PersonalAccountAdmin;
import com.example.library.R;
import com.example.library.db.Application;
import com.example.library.db.FirebaseDatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdapterAppAdmin extends RecyclerView.Adapter<AdapterAppAdmin.NewViewHolder> {
    private final Context context;
    private List<Application> dataApplication = new ArrayList<>();
    private final FirebaseDatabaseManager databaseManager;
    private String surname;
    private String name;
    private String patronymic;

    public AdapterAppAdmin(Context context, FirebaseDatabaseManager databaseManager) {
        this.context = context;
        this.databaseManager = databaseManager;
    }

    public void setDataWithFullName(String surname, String name, String patronymic) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }

    @NonNull
    @Override
    public AdapterAppAdmin.NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_admin, parent, false);
        return new AdapterAppAdmin.NewViewHolder(view, context, dataApplication);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAppAdmin.NewViewHolder holder, int position) {
        Application application = dataApplication.get(position);
        holder.data.setText(application.getBookName() + "\nКоличество к выдаче:" + application.getQuantity() + "\n" + application.getStatus());
        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateApplicationStatus(application);
                notifyItemChanged(position);
            }
        });

    }

    private void updateApplicationStatus(Application application) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String nameFromDB = userSnapshot.child("name").getValue(String.class);
                    String patronymicFromDB = userSnapshot.child("patronymic").getValue(String.class);
                    String surnameFromDB = userSnapshot.child("surname").getValue(String.class);

                    if (nameFromDB.equals(name) && patronymicFromDB.equals(patronymic) && surnameFromDB.equals(surname)) {
                        // Удаление заявки из списка applications
                        DatabaseReference applicationsRef = userSnapshot.child("applications").getRef();
                        Query query = applicationsRef.orderByChild("bookName").equalTo(application.getBookName());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (application.getStatus().equals("в обработке")) {
                                        snapshot.getRef().child("status").setValue("готово к выдаче");
                                    } else {
                                        snapshot.getRef().removeValue();
                                        // Создание нового уникального узла для информации о заявке в списке ticket
                                        DatabaseReference ticketRef = userSnapshot.child("ticket").child(usersRef.push().getKey()).getRef();
                                        ticketRef.child("bookName").setValue(application.getBookName());
                                        ticketRef.child("quantity").setValue(application.getQuantity());

                                        // Установка даты начала и окончания аренды
                                        Calendar calendar = Calendar.getInstance();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                        String currentDate = dateFormat.format(calendar.getTime());
                                        ticketRef.child("dataStart").setValue(currentDate);

                                        calendar.add(Calendar.MONTH, 1); // Добавляем месяц
                                        String endDate = dateFormat.format(calendar.getTime());
                                        ticketRef.child("dataEnd").setValue(endDate);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Обработка ошибки
                            }
                        });

                        Toast.makeText(context, "Статус заявки обновлен", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки
            }
        });
    }



    @Override
    public int getItemCount() {
        return dataApplication.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Application> applications) {
        this.dataApplication = applications;
        notifyDataSetChanged();
    }

    static class NewViewHolder extends RecyclerView.ViewHolder {
        private final TextView data;
        private final ImageButton ok;
        private final List<Application> dataApplication;
        private final Context context;

        public NewViewHolder(@NonNull View itemView, Context context, List<Application> applications) {
            super(itemView);
            this.context = context;
            data = itemView.findViewById(R.id.dataApplication);
            ok = itemView.findViewById(R.id.imageButton);
            this.dataApplication = applications;
        }
    }
}