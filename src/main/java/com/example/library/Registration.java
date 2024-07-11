package com.example.library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.db.FirebaseDatabaseManager;
import com.example.library.db.User;

public class Registration extends AppCompatActivity {
    private FirebaseDatabaseManager databaseManager;
    private EditText edtLogin;
    private EditText edtPassword;
    private EditText edtSurname;
    private EditText edtName;
    private EditText edtPatronymic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setStatusBarColor(R.color.golden);

        init();

        databaseManager = FirebaseDatabaseManager.getInstance();
    }

    private void init(){
        edtLogin = findViewById(R.id.edtLogin);
        edtPassword = findViewById(R.id.edtPassword);
        edtSurname = findViewById(R.id.edtSurname);
        edtName = findViewById(R.id.edtName);
        edtPatronymic = findViewById(R.id.edtPatronymic);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void setStatusBarColor(int color) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { return; }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, color));
    }

    public void onClickToActivityMain(View V){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onClickReg(View V){
        String login = edtLogin.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String surname = edtSurname.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String patronymic = edtPatronymic.getText().toString().trim();

        boolean allFieldsValid = true;

        if (TextUtils.isEmpty(login)) {
            edtLogin.setError("Заполните поле");
            allFieldsValid = false;
        } else {
            edtLogin.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Заполните поле");
            allFieldsValid = false;
        } else {
            edtPassword.setError(null);
        }
        if (TextUtils.isEmpty(surname)) {
            edtSurname.setError("Заполните поле");
            allFieldsValid = false;
        } else {
            edtSurname.setError(null);
        }
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Заполните поле");
            allFieldsValid = false;
        } else {
            edtName.setError(null);
        }
        if (TextUtils.isEmpty(patronymic)) {
            edtPatronymic.setError("Заполните поле");
            allFieldsValid = false;
        } else {
            edtPatronymic.setError(null);
        }

        if (allFieldsValid) {
            User user = new User(login, password, surname, name, patronymic, false);
            databaseManager.writeDataToDatabase("users", user);
            Toast.makeText(getApplicationContext(), "Успешно!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }


}