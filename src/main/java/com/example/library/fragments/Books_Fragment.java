package com.example.library.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.library.R;
import com.example.library.adapters.AdapterBooks;
import com.example.library.db.FirebaseDatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Books_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Books_Fragment extends Fragment {
    private RecyclerView list;
    private AdapterBooks adapter;
    private final FirebaseDatabaseManager databaseManager = FirebaseDatabaseManager.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Books_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Books_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Books_Fragment newInstance(String param1, String param2) {
        Books_Fragment fragment = new Books_Fragment();
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
        View view = inflater.inflate(R.layout.fragment_books_, container, false);
        list = view.findViewById(R.id.listB);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        loadBooksFromDatabase();
        setupSearchView();
    }

    private void init() {
        adapter = new AdapterBooks(getContext(), databaseManager);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
    }

    private void setupSearchView() {
        SearchView searchView = requireView().findViewById(R.id.search_book);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchBooks(newText);
                return true;
            }
        });
    }

    private void loadBooksFromDatabase() {
        databaseManager.readDataFromDatabase("books", new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                ArrayList<Boolean> arrayButtonVisibility = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String name = userSnapshot.child("name").getValue(String.class);
                    String author = userSnapshot.child("author").getValue(String.class);
                    String image = userSnapshot.child("image").getValue(String.class);
                    String descriptionBook = generateBookDescription(userSnapshot);

                    if (name != null && image != null) {
                        names.add(name);
                        images.add(image);
                        descriptions.add(descriptionBook);

                        Integer count = userSnapshot.child("count").getValue(Integer.class);
                        arrayButtonVisibility.add(count != null && count > 0);
                    }
                }
                adapter.setBooksData(names, images, descriptions, arrayButtonVisibility);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String generateBookDescription(DataSnapshot userSnapshot) {
        return "автор: " + userSnapshot.child("author").getValue(String.class) + "\n"
                + "количество в библиотеке : " + userSnapshot.child("count").getValue(Integer.class);
    }

    private void searchBooks(String searchText) {
        databaseManager.readDataFromDatabase("books", new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                ArrayList<Boolean> arrayButtonVisibility = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String name = userSnapshot.child("name").getValue(String.class);
                    String author = userSnapshot.child("author").getValue(String.class);
                    String image = userSnapshot.child("image").getValue(String.class);
                    String descriptionBook = generateBookDescription(userSnapshot);

                    if (name != null && image != null && (name.toLowerCase().contains(searchText.toLowerCase())
                            || author.toLowerCase().contains(searchText.toLowerCase()))) {
                        names.add(name);
                        images.add(image);
                        descriptions.add(descriptionBook);

                        Integer count = userSnapshot.child("count").getValue(Integer.class);
                        arrayButtonVisibility.add(count != null && count > 0);
                    }
                }
                adapter.setBooksData(names, images, descriptions, arrayButtonVisibility);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}