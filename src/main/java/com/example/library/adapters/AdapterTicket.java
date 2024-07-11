package com.example.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;
import com.example.library.db.FirebaseDatabaseManager;
import com.example.library.db.Ticket;

import java.util.ArrayList;
import java.util.List;

public class AdapterTicket extends RecyclerView.Adapter<AdapterTicket.NewViewHolder> {
    private final Context context;
    private List<Ticket> dataTicket = new ArrayList<>();
    private final FirebaseDatabaseManager databaseManager;

    public AdapterTicket(Context context, FirebaseDatabaseManager databaseManager) {
        this.context = context;
        this.databaseManager = databaseManager;
    }

    @NonNull
    @Override
    public AdapterTicket.NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_ticket, parent, false);
        return new AdapterTicket.NewViewHolder(view, context, dataTicket);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTicket.NewViewHolder holder, int position) {
        Ticket ticket = dataTicket.get(position);
        holder.data.setText(ticket.getBookName() + "\nДата получения:" + ticket.getDataStart() +
               "\nДата сдачи:" + ticket.getDataEnd() +
               "\nКоличество к сдаче:" + ticket.getQuantity());

    }

    @Override
    public int getItemCount() {
        return dataTicket.size();
    }

    public void setData(List<Ticket> tickets) {
        this.dataTicket = tickets;
    }

    static class NewViewHolder extends RecyclerView.ViewHolder {
        private final TextView data;
        private final List<Ticket> tickets;
        private final Context context;

        public NewViewHolder(@NonNull View itemView, Context context, List<Ticket> tickets) {
            super(itemView);
            this.context = context;
            data = itemView.findViewById(R.id.itemTicket);
            this.tickets = tickets;
        }
    }
}