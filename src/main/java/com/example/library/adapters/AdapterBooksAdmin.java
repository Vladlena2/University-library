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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.library.AddBook;
import com.example.library.Catalog;
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

// ПРОВЕРКА ЕСЛИ НЕТ ДАННЫХ!!!

public class AdapterBooksAdmin extends RecyclerView.Adapter<AdapterBooksAdmin.NewViewHolder> {
    private final Context context;
    private ArrayList<String> arrayNameBooks = new ArrayList<>();
    private ArrayList<String> arrayImageBooks = new ArrayList<>();
    private ArrayList<String> arrayDescriptions = new ArrayList<>();
    private ArrayList<String> arrayAuthor = new ArrayList<>();
    private ArrayList<Integer> arrayCount = new ArrayList<>();
    private FirebaseDatabaseManager databaseManager;

    public AdapterBooksAdmin(Context context, FirebaseDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterBooksAdmin.NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_books_a, parent, false);
        return new AdapterBooksAdmin.NewViewHolder(view, context, arrayNameBooks, arrayImageBooks, arrayDescriptions);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBooksAdmin.NewViewHolder holder, int position) {
        holder.nameBook.setText(arrayNameBooks.get(position));
        holder.description.setText(arrayDescriptions.get(position));
        String imageUrl = arrayImageBooks.get(position);
        Glide.with(context).load(imageUrl).into(holder.imageBook);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(context, AddBook.class);
                editIntent.putExtra("bookName", arrayNameBooks.get(holder.getAdapterPosition()));
                editIntent.putExtra("author", arrayAuthor.get(holder.getAdapterPosition()));
                editIntent.putExtra("count", String.valueOf(arrayCount.get(holder.getAdapterPosition()))); // Преобразование целочисленного значения в строку
                editIntent.putExtra("imageUrl", arrayImageBooks.get(holder.getAdapterPosition()));
                // Добавьте остальные данные о книге, если необходимо
                context.startActivity(editIntent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookName = arrayNameBooks.get(holder.getAdapterPosition());
                removeBookFromList(bookName);
                removeBookFromDatabase(bookName);
            }
        });
    }

    private void removeBookFromList(String bookName) {
        int position = arrayNameBooks.indexOf(bookName);
        if (position != -1) {
            arrayNameBooks.remove(position);
            arrayImageBooks.remove(position);
            arrayDescriptions.remove(position);
            arrayAuthor.remove(position);
            arrayCount.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void removeBookFromDatabase(String bookName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("books");
        Query query = databaseReference.orderByChild("name").equalTo(bookName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayNameBooks.size();
    }

    static class NewViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameBook;
        private final TextView description;
        private final ImageView imageBook;
        private final Button edit;
        private final Button delete;
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
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            this.arrayNameBooks = array;
            this.arrayImageBooks = array2;
            this.arrayDescriptions = array3;
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBooksData(ArrayList<String> names, ArrayList<String> images, ArrayList<String> description, ArrayList<String> author, ArrayList<Integer> count) {
        arrayNameBooks.clear();
        arrayNameBooks.addAll(names);
        arrayImageBooks.clear();
        arrayImageBooks.addAll(images);
        arrayDescriptions.clear();
        arrayDescriptions.addAll(description);
        arrayAuthor.clear();
        arrayAuthor.addAll(author);
        arrayCount.clear();
        arrayCount.addAll(count);
        notifyDataSetChanged();
    }
}


