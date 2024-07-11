package com.example.library.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.library.R;
import com.example.library.adapters.AdapterApplications;
import com.example.library.adapters.AdapterBooks;
import com.example.library.adapters.AdapterTicket;
import com.example.library.db.Application;
import com.example.library.db.FirebaseDatabaseManager;
import com.example.library.db.Ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Personal_Account_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Personal_Account_Fragment extends Fragment {
    private RecyclerView listApplication;
    private RecyclerView listTicket;
    private AdapterApplications adapterApplication;
    private AdapterTicket adapterTicket;
    private final FirebaseDatabaseManager databaseManager = FirebaseDatabaseManager.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Personal_Account_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Personal_Account_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Personal_Account_Fragment newInstance(String param1, String param2) {
        Personal_Account_Fragment fragment = new Personal_Account_Fragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        fetchUserApplications();
    }

    private void init() {
        adapterApplication = new AdapterApplications(getContext(), databaseManager);
        listApplication.setLayoutManager(new LinearLayoutManager(requireContext()));
        listApplication.setAdapter(adapterApplication);
        adapterTicket = new AdapterTicket(getContext(), databaseManager);
        listTicket.setLayoutManager(new LinearLayoutManager(requireContext()));
        listTicket.setAdapter(adapterTicket);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal__account_, container, false);
        listApplication = view.findViewById(R.id.recyclerViewApplications);
        listTicket = view.findViewById(R.id.recyclerViewBooks);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("my_preferences2", Context.MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "");

        // Установка имени пользователя в TextView
        TextView textViewUserFullName = view.findViewById(R.id.textViewStudentName);
        textViewUserFullName.setText(fullName);

        return view;
    }

    private void fetchUserApplications() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String login = sharedPreferences.getString("login", "");

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = usersRef.orderByChild("login").equalTo(login);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Application> userApplications = new ArrayList<>();
                List<Ticket> userTickets = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Получаем приложения пользователя
                    DataSnapshot applicationsSnapshot = userSnapshot.child("applications");
                    for (DataSnapshot applicationSnapshot : applicationsSnapshot.getChildren()) {
                        Application application = applicationSnapshot.getValue(Application.class);
                        userApplications.add(application);
                    }

                    // Получаем билеты пользователя
                    DataSnapshot ticketsSnapshot = userSnapshot.child("ticket");
                    for (DataSnapshot ticketSnapshot : ticketsSnapshot.getChildren()) {
                        Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                        userTickets.add(ticket);
                    }
                }

                // Ваши действия с данными, например, установка данных в адаптеры
                adapterApplication.setData(userApplications);
                adapterTicket.setData(userTickets);

                adapterApplication.notifyDataSetChanged();
                adapterTicket.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки
            }
        });
    }

}