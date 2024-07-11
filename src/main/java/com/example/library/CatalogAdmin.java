package com.example.library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.library.fragments.BookAdmin;
import com.example.library.fragments.Books_Fragment;
import com.example.library.fragments.Personal_Account_Fragment;
import com.example.library.fragments.TicketAdmin;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CatalogAdmin extends AppCompatActivity {
    private Fragment fragment_books, fragment_ticket;
    private FragmentManager fragmentManager;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_admin);

        fragmentManager = getSupportFragmentManager();

        // Инициализация фрагментов
        fragment_books = new BookAdmin();
        fragment_ticket = new TicketAdmin();

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
                    selectedFragment = fragment_ticket;
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