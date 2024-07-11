package com.example.library.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.library.AppListAdmin;
import com.example.library.Catalog;
import com.example.library.PersonalAccountAdmin;
import com.example.library.R;
import com.example.library.adapters.AdapterUser;
import com.example.library.db.FirebaseDatabaseManager;
import com.example.library.db.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketAdmin extends Fragment {
    private RecyclerView recyclerView;
    private AdapterUser adapter;
    private final List<User> userList = new ArrayList<>();
    private final FirebaseDatabaseManager databaseManager = FirebaseDatabaseManager.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TicketAdmin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TicketAdmin.
     */
    // TODO: Rename and change types and number of parameters
    public static TicketAdmin newInstance(String param1, String param2) {
        TicketAdmin fragment = new TicketAdmin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_admin, container, false);
        recyclerView = view.findViewById(R.id.listUsers);
        setupRecyclerView();
        setupSearchView(view);
        loadUsersFromDatabase();
        return view;
    }

    private void setupRecyclerView() {
        adapter = new AdapterUser(getContext(), databaseManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.search_users);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUserList(newText);
                return true;
            }
        });
    }

    private void loadUsersFromDatabase() {
        databaseManager.readDataFromDatabase("users", new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String surname = userSnapshot.child("surname").getValue(String.class);
                    String name = userSnapshot.child("name").getValue(String.class);
                    String patronymic = userSnapshot.child("patronymic").getValue(String.class);
                    boolean admin = userSnapshot.child("admin").getValue(Boolean.class);
                    if (!admin) {
                        User user = new User(surname, name, patronymic);
                        userList.add(user);
                    }
                }
                adapter.setUserList(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок при чтении данных из базы данных
            }
        });
    }

    private void filterUserList(String newText) {
        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getFullName().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.setUserList(filteredList);
    }

}