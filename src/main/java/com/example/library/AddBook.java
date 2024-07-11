package com.example.library;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddBook extends AppCompatActivity {
    private EditText nameTitle, nameTitle6, nameTitle9, nameTitle2;
    private ImageButton btnSave;
    // Добавьте поля для остальных данных о книге, если необходимо

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        init();

        // Получаем данные о книге из Intent
        Intent intent = getIntent();
        if (intent != null) {
            String bookName = intent.getStringExtra("bookName");
            String author = intent.getStringExtra("author");
            String count = intent.getStringExtra("count");
            String imageUri = intent.getStringExtra("imageUrl"); // Путь к изображению книги

            // Получите остальные данные о книге таким же образом

            // Заполните соответствующие поля формы данными о книге
            nameTitle.setText(bookName);
            nameTitle6.setText(author);
            nameTitle9.setText(count);
            nameTitle2.setText(imageUri);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Получаем данные из полей формы
                    String bookName = nameTitle.getText().toString();
                    String author = nameTitle6.getText().toString();
                    String imageString = nameTitle2.getText().toString();
                    String countString = nameTitle9.getText().toString();
                    // Проверяем, что все поля заполнены
                    if (bookName.isEmpty() || author.isEmpty() || imageString.isEmpty() || countString.isEmpty()) {
                        // Если хотя бы одно поле не заполнено, выводим сообщение об ошибке
                        Toast.makeText(AddBook.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Проверяем, что countString содержит только числа
                    if (!countString.matches("\\d+")) {
                        // Если countString не содержит только числа, выводим сообщение об ошибке
                        Toast.makeText(AddBook.this, "Количество должно быть числом", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Преобразуем строку countString в целое число
                    int count = Integer.parseInt(countString);

                    // Создаем новый объект книги
                    Map<String, Object> bookData = new HashMap<>();
                    bookData.put("name", bookName);
                    bookData.put("author", author);
                    bookData.put("image", imageString);
                    bookData.put("count", count);

                    // Добавьте остальные данные о книге, если необходимо

                    // Получаем ссылку на базу данных книг
                    DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("books");

                    // Выполняем запрос к базе данных для поиска книги по названию
                    Query query = booksRef.orderByChild("name").equalTo(bookName);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // Получаем идентификатор книги
                                String bookId = snapshot.getKey();

                                // Обновляем данные книги
                                booksRef.child(bookId).setValue(bookData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent i = new Intent(AddBook.this, CatalogAdmin.class);
                                                    startActivity(i);
                                                } else {
                                                    // Обработка ошибки при сохранении данных
                                                    Toast.makeText(AddBook.this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Обработка ошибки при чтении данных из базы данных
                        }
                    });
                }
            });

        }
    }

    private void init() {
        nameTitle = findViewById(R.id.nameTitle);
        nameTitle6 = findViewById(R.id.nameTitle6);
        nameTitle9 = findViewById(R.id.nameTitle9);
        nameTitle2 = findViewById(R.id.nameTitle2);
        btnSave = findViewById(R.id.btnSave);
        // Инициализируйте остальные поля формы
    }

    public void onClickToCatalog(View v) {
        Intent i = new Intent(this, CatalogAdmin.class);
        startActivity(i);
    }
}
