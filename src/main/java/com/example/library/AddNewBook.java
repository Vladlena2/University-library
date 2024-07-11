package com.example.library;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.library.db.Book;
import com.example.library.db.FirebaseDatabaseManager;

public class AddNewBook extends AppCompatActivity {

    private EditText nameTitle, nameTitle6, nameTitle9, nameTitle2;
    private ImageButton btnSave;
    private final FirebaseDatabaseManager databaseManager = FirebaseDatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_book);

        initViews();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewBookToDatabase();
            }
        });
    }

    private void initViews() {
        nameTitle = findViewById(R.id.nameTitle);
        nameTitle6 = findViewById(R.id.nameTitle6);
        nameTitle9 = findViewById(R.id.nameTitle9);
        nameTitle2 = findViewById(R.id.nameTitle2);
        btnSave = findViewById(R.id.btnSave);
    }

    private void saveNewBookToDatabase() {
        String bookName = nameTitle.getText().toString();
        String author = nameTitle6.getText().toString();
        String countString = nameTitle9.getText().toString();
        String imageUri = nameTitle2.getText().toString();

        // Проверяем, что все поля заполнены
        if (bookName.isEmpty() || author.isEmpty() || countString.isEmpty() || imageUri.isEmpty()) {
            // Если хотя бы одно поле не заполнено, выводим сообщение об ошибке
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем, что countString содержит только числа
        if (!countString.matches("\\d+")) {
            // Если countString не содержит только числа, выводим сообщение об ошибке
            Toast.makeText(this, "Количество должно быть числом", Toast.LENGTH_SHORT).show();
            return;
        }

        // Преобразуем строку countString в целое число
        int count = Integer.parseInt(countString);

        // Создаем новый объект книги
        Book newBook = new Book(bookName, count, imageUri, author);

        // Записываем данные новой книги в базу данных
        databaseManager.writeDataToDatabase("books", newBook);

        // После успешной записи данных можно выполнить какие-то дополнительные действия,
        // например, перейти на другой экран или вывести сообщение об успешном сохранении
        Intent i = new Intent(this, CatalogAdmin.class);
        startActivity(i);
    }


    public void onClickToCatalog(View v) {
        Intent i = new Intent(this, CatalogAdmin.class);
        startActivity(i);
    }
}
