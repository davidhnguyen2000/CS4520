package com.example.inclassassignments.InClass08and09;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inclassassignments.R;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private ArrayList<AuthUser> chats;
    private IFromAuthUserAdapter sendData;
    private String currentUsername;

    public ChatAdapter(Context context, ArrayList<AuthUser> chats, String currentUsername) {
        this.chats = chats;
        this.currentUsername = currentUsername;
        if (context instanceof IFromAuthUserAdapter) {
            sendData = (IFromAuthUserAdapter) context;
        } else {
            throw new RuntimeException(context + " must implement IFromAuthUserAdapter");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewFirst;
        private final TextView textViewLast;
        private final CardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewFirst = itemView.findViewById(R.id.textViewFirstName);
            this.textViewLast = itemView.findViewById(R.id.textViewLastName);
            this.card = itemView.findViewById(R.id.cardViewChat);
        }

        public TextView getTextViewFirst() {
            return textViewFirst;
        }

        public TextView getTextViewLast() {
            return textViewLast;
        }

        public CardView getCard() {
            return card;
        }
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.getTextViewFirst().setText(chats.get(position).getFirstName());
        holder.getTextViewLast().setText(chats.get(position).getLastName());
        holder.getCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.chatSelected(chats.get(holder.getAdapterPosition()), currentUsername);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public interface IFromAuthUserAdapter {
        void chatSelected(AuthUser chatUser, String currentUsername);
    }
}


