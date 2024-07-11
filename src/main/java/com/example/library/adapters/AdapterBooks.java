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

import com.bumptech.glide.Glide;
import com.example.library.R;
import com.example.library.db.FirebaseDatabaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterBooks extends RecyclerView.Adapter<AdapterBooks.NewViewHolder> {
    private final Context context;
    private ArrayList<String> arrayNameBooks = new ArrayList<>();
    private ArrayList<String> arrayImageBooks = new ArrayList<>();
    private ArrayList<String> arrayDescriptions = new ArrayList<>();
    private ArrayList<Boolean> arrayButtonVisibility = new ArrayList<>();
    private FirebaseDatabaseManager databaseManager;

    public AdapterBooks(Context context, FirebaseDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterBooks.NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_books, parent, false);
        return new AdapterBooks.NewViewHolder(view, context, arrayNameBooks, arrayImageBooks, arrayDescriptions);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBooks.NewViewHolder holder, int position) {
        holder.nameBook.setText(arrayNameBooks.get(position));
        holder.description.setText(arrayDescriptions.get(position));
        String imageUrl = arrayImageBooks.get(position);
        Glide.with(context).load(imageUrl).into(holder.imageBook);

        // Установка видимости кнопки на основе значения из arrayButtonVisibility
        if (arrayButtonVisibility.size() > position && arrayButtonVisibility.get(position)) {
            holder.reserve.setVisibility(View.VISIBLE);
        } else {
            holder.reserve.setVisibility(View.GONE);
        }

        holder.reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем имя книги для текущего элемента
                String bookName = arrayNameBooks.get(holder.getAdapterPosition());
                // Уменьшаем значение атрибута "count" в базе данных на 1
                decrementBookCount(bookName, holder.getAdapterPosition());
            }
        });

    }

    private void decrementBookCount(String bookName, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String login = sharedPreferences.getString("login", "");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference booksRef = rootRef.child("books");

        Query query = booksRef.orderByChild("name").equalTo(bookName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Integer count = bookSnapshot.child("count").getValue(Integer.class);
                    if (count != null && count > 0) {
                        decreaseBookCount(bookSnapshot.getRef(), count, position, login, bookName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void decreaseBookCount(DatabaseReference bookRef, int count, int position, String login, String bookName) {
        bookRef.child("count").setValue(count - 1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addApplicationsNodeForUser(login, bookName);
                    updateAdapter(position);
                }
            }
        });
    }

    private void addApplicationsNodeForUser(String userLogin, String bookName) {
        // Получаем ссылку на базу данных Firebase
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Находим пользователя по логину
        Query query = rootRef.child("users").orderByChild("login").equalTo(userLogin);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Проверяем, найден ли пользователь с таким логином
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Получаем ключ пользователя
                        String userKey = userSnapshot.getKey();

                        // Создаем узел "applications" для этого пользователя
                        DatabaseReference userApplicationsRef = rootRef.child("users").child(userKey).child("applications");

                        // Проверяем, существует ли уже запись для этой книги
                        Query bookQuery = userApplicationsRef.orderByChild("bookName").equalTo(bookName);
                        bookQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                                        // Увеличиваем значение quantity на 1
                                        int quantity = bookSnapshot.child("quantity").getValue(Integer.class);
                                        bookSnapshot.getRef().child("quantity").setValue(quantity + 1);
                                    }
                                } else {
                                    // Создаем новую запись
                                    Map<String, Object> applicationData = new HashMap<>();
                                    applicationData.put("bookName", bookName);
                                    applicationData.put("quantity", 1);
                                    applicationData.put("status", "в обработке");
                                    userApplicationsRef.push().setValue(applicationData);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void updateAdapter(int position) {
        int lastIndex = arrayDescriptions.get(position).lastIndexOf(" "); // Находим индекс последнего пробела
        int count = Integer.parseInt(arrayDescriptions.get(position).substring(lastIndex + 1)); // Получаем значение счетчика
        count--; // Уменьшаем счетчик на 1
        if (count >= 0) { // Проверяем, что счетчик не меньше нуля
            arrayDescriptions.set(position, arrayDescriptions.get(position).substring(0, lastIndex) + " " + count); // Обновляем значение счетчика в списке
            if (count == 0) {
                arrayButtonVisibility.set(position, false); // Если счетчик достиг нуля, скрываем кнопку
            }
            notifyDataSetChanged(); // Уведомляем адаптер об изменении данных
        }
    }



    @Override
    public int getItemCount() {
        return arrayNameBooks.size();
    }

    static class NewViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameBook;
        private final TextView description;
        private final ImageView imageBook;
        private final Button reserve;
        private final List<String> arrayNameBooks;
        private final List<String> arrayImageBooks;
        private final List<String> arrayDescriptions;
        private final Context context;

        public NewViewHolder(@NonNull View itemView, Context context, List<String> array, List<String> array2,  List<String> array3) {
            super(itemView);
            this.context = context;
            nameBook = itemView.findViewById(R.id.nameBook);
            description =  itemView.findViewById(R.id.description);
            imageBook = itemView.findViewById(R.id.imageBook);
            reserve = itemView.findViewById(R.id.reserve);
            this.arrayNameBooks = array;
            this.arrayImageBooks = array2;
            this.arrayDescriptions = array3;
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBooksData(ArrayList<String> names, ArrayList<String> images, ArrayList<String> description, ArrayList<Boolean> buttonVisibility) {
        arrayNameBooks.clear();
        arrayNameBooks.addAll(names);
        arrayImageBooks.clear();
        arrayImageBooks.addAll(images);
        arrayDescriptions.clear();
        arrayDescriptions.addAll(description);
        arrayButtonVisibility.clear();
        arrayButtonVisibility.addAll(buttonVisibility);
        notifyDataSetChanged();
    }

}

