package com.example.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;
import com.example.library.db.FirebaseDatabaseManager;
import com.example.library.db.Ticket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterTicketAdmin extends RecyclerView.Adapter<AdapterTicketAdmin.NewViewHolder> {
    private final Context context;
    private List<Ticket> dataTicket = new ArrayList<>();
    private final FirebaseDatabaseManager databaseManager;
    private String id;

    public AdapterTicketAdmin(Context context, FirebaseDatabaseManager databaseManager) {
        this.context = context;
        this.databaseManager = databaseManager;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_admin, parent, false);
        return new NewViewHolder(view);
    }

    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(id).child("ticket");

        Ticket ticket = dataTicket.get(position);
        holder.data.setText(ticket.getBookName() + "\nДата получения:" + ticket.getDataStart() +
                "\nДата сдачи:" + ticket.getDataEnd() +
                "\nКоличество к сдаче:" + ticket.getQuantity());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ticketKey = ticket.getId();
                DatabaseReference ticketRef = usersRef.child(ticketKey);
                ticketRef.getRef().removeValue();
                ticketRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dataTicket.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                Toast.makeText(context, "Данные успешно удалены", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Ошибка при удалении данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataTicket.size();
    }

    public void setData(List<Ticket> tickets, String id) {
        this.dataTicket = tickets;
        this.id = id;
        notifyDataSetChanged();
    }

    static class NewViewHolder extends RecyclerView.ViewHolder {
        private final TextView data;
        private final ImageButton deleteButton;

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.dataApplication);
            deleteButton = itemView.findViewById(R.id.imageButton);
        }
    }
}
