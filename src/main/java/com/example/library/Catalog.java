package com.example.library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.library.fragments.Books_Fragment;
import com.example.library.fragments.Personal_Account_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Catalog extends AppCompatActivity {
    private Fragment fragment_books, fragment_person;
    private FragmentManager fragmentManager;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        fragmentManager = getSupportFragmentManager();

        // Инициализация фрагментов
        fragment_books = new Books_Fragment();
        fragment_person = new Personal_Account_Fragment();

        // Установка начального фрагмента
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment_books).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.action_item1:
                    selectedFragment = fragment_books;
                    break;
                case R.id.action_item2:
                    selectedFragment = fragment_person;
                    break;
            }
            if (selectedFragment != null) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
            return false;
        });
    }
}
