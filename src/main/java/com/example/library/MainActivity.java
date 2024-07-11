package com.example.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.library.db.FirebaseDatabaseManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabaseManager databaseManager;
    private EditText edPassword;
    private EditText edLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarColor(R.color.white);

        // Инициализация Firebase перед обращением к базе данных
        FirebaseApp.initializeApp(this);

        init();

        // Инициализация базы данных Firebase после инициализации Firebase
        databaseManager = FirebaseDatabaseManager.getInstance();
    }

    private void init() {
        edLog = findViewById(R.id.edLog);
        edPassword = findViewById(R.id.edPassword);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, color));
    }

    public void toReg(View v) {
        ConstraintLayout ovalLayout = findViewById(R.id.oval);

        // Получаем высоту экрана
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Создаем аниматор для перемещения вниз
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(ovalLayout, "translationY", 0, screenHeight);
        translationAnimator.setDuration(1000); // Устанавливаем длительность анимации

        // Создаем аниматор для изменения прозрачности
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(ovalLayout, "alpha", 1.0f, 0.0f);
        alphaAnimator.setDuration(1000); // Устанавливаем длительность анимации

        // Создаем аниматоры для двух анимаций
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationAnimator, alphaAnimator); // Запускаем анимации одновременно

        // Добавляем слушатель завершения анимации
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Запускаем переход на следующий экран после завершения анимации
                Intent i = new Intent(MainActivity.this, Registration.class);
                startActivity(i);
            }
        });

        // Запускаем анимацию
        animatorSet.start();
    }

    public void toActivityCatalog() {
        Intent i = new Intent(this, Catalog.class);
        startActivity(i);
    }

    public void onClickToActivityCatalog(View v) {
        String login = edLog.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (TextUtils.isEmpty(login)) {
            edLog.setError("Заполните поле");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edPassword.setError("Заполните поле");
            return;
        }

        databaseManager.readDataFromDatabase("users", new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;
                boolean isAdmin = false;
                boolean passwordCorrect = false; // Добавляем флаг для проверки корректности пароля
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userLogin = userSnapshot.child("login").getValue(String.class);
                    if (userLogin != null && userLogin.equals(login)) {
                        userFound = true;
                        String userPassword = userSnapshot.child("password").getValue(String.class);
                        if (userPassword != null && userPassword.equals(password)) {
                            passwordCorrect = true; // Устанавливаем флаг, если пароль верный
                            String firstName = userSnapshot.child("name").getValue(String.class);
                            String lastName = userSnapshot.child("surname").getValue(String.class);
                            String middleName = userSnapshot.child("patronymic").getValue(String.class);
                            String fullName = lastName + " " + firstName + " " + middleName;

                            // Сохраняем данные пользователя в SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("login", login);
                            editor.apply();

                            SharedPreferences sharedPreferences2 = getSharedPreferences("my_preferences2", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                            editor2.putString("fullName", fullName);
                            editor2.apply();

                            isAdmin = userSnapshot.child("admin").getValue(Boolean.class);
                            break;
                        } else {
                            edPassword.setError("Неверный пароль");
                        }
                    }
                }
                if (!userFound) {
                    edLog.setError("Пользователь не найден");
                } else if (passwordCorrect) { // Проверяем флаг корректности пароля
                    if (isAdmin) {
                        Intent intent = new Intent(MainActivity.this, CatalogAdmin.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, Catalog.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки чтения данных из базы данных
            }
        });

    }



}
