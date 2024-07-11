package com.example.library.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;
import com.example.library.db.Application;
import com.example.library.db.FirebaseDatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterApplications extends RecyclerView.Adapter<AdapterApplications.NewViewHolder> {
    private final Context context;
    private List<Application> dataApplication = new ArrayList<>();
    private final FirebaseDatabaseManager databaseManager;

    public AdapterApplications(Context context, FirebaseDatabaseManager databaseManager) {
        this.context = context;
        this.databaseManager = databaseManager;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_application, parent, false);
        return new NewViewHolder(view, context, dataApplication);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
        Application application = dataApplication.get(position);
        holder.data.setText(application.getBookName() + "\nКоличество к выдаче:" + application.getQuantity() + "\n" + application.getStatus());

        // Проверяем статус книги
        if (application.getStatus().equals("готово к выдаче")) {
            // Если статус книги "готово к выдаче", делаем кнопку невидимой
            holder.delete.setVisibility(View.INVISIBLE);
        } else {
            // В противном случае, делаем кнопку видимой
            holder.delete.setVisibility(View.VISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                String login = sharedPreferences.getString("login", "");
                // Получаем позицию элемента, на который нажали
                int adapterPosition = holder.getAdapterPosition();

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference booksRef = rootRef.child("users");

                Query query = booksRef.orderByChild("login").equalTo(login);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            // Переход в узел "application" для найденного пользователя
                            DatabaseReference applicationRef = userSnapshot.getRef().child("applications");

                            // Выполнение вложенного запроса для поиска элемента по "bookName"
                            Query bookQuery = applicationRef.orderByChild("bookName").equalTo(application.getBookName());

                            bookQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                                        // Удаление найденного элемента по "bookName"
                                        bookSnapshot.getRef().removeValue();
                                        dataApplication.remove(adapterPosition);
                                        notifyDataSetChanged();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return dataApplication.size();
    }

    public void setData(List<Application> applications) {
        this.dataApplication = applications;
    }

    static class NewViewHolder extends RecyclerView.ViewHolder {
        private final TextView data;
        private final ImageView delete;
        private final List<Application> dataApplication;
        private final Context context;

        public NewViewHolder(@NonNull View itemView, Context context, List<Application> applications) {
            super(itemView);
            this.context = context;
            data = itemView.findViewById(R.id.dataApplication);
            delete = itemView.findViewById(R.id.imageButton);
            this.dataApplication = applications;
        }
    }
}

