package com.example.library.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.PersonalAccountAdmin;
import com.example.library.R;
import com.example.library.db.FirebaseDatabaseManager;
import com.example.library.db.User;

import java.util.ArrayList;
import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.NewViewHolder> {
    private final Context context;
    private List<User> userList;
    private List<User> userListFiltered;
    private final FirebaseDatabaseManager databaseManager;

    public AdapterUser(Context context, FirebaseDatabaseManager databaseManager) {
        this.context = context;
        this.databaseManager = databaseManager;
        userList = new ArrayList<>();
        userListFiltered = new ArrayList<>();
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_users, parent, false);
        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
        User user = userListFiltered.get(position);
        holder.fullNameTextView.setText(user.getFullName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = user.getFullName();
                Intent intent = new Intent(context, PersonalAccountAdmin.class);
                intent.putExtra("fullName", fullName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userListFiltered.size();
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        this.userListFiltered = new ArrayList<>(userList);
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<User> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    // Если фильтр пустой, отображаем полный список
                    filteredList.addAll(userList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (User user : userList) {
                        if (user.getFullName().toLowerCase().contains(filterPattern)) {
                            // Добавляем пользователя в отфильтрованный список, если его имя соответствует фильтру
                            filteredList.add(user);
                        }
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userListFiltered.clear();
                userListFiltered.addAll((List<User>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    static class NewViewHolder extends RecyclerView.ViewHolder {
        private final TextView fullNameTextView;

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.itemTicket);
        }
    }
}
