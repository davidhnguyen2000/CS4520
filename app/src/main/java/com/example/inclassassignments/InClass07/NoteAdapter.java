package com.example.inclassassignments.InClass07;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inclassassignments.R;
import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private ArrayList<Note> notes;
    private IDeleteNote sendData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewText;
        private final Button buttonDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewText = itemView.findViewById(R.id.textViewNoteText);
            this.buttonDelete = itemView.findViewById(R.id.buttonDeleteNote);
        }

        public TextView getTextViewText() {
            return textViewText;
        }

        public Button getButtonDelete() {
            return buttonDelete;
        }
    }

    public NoteAdapter(Fragment fragment) {
        if (fragment instanceof IDeleteNote) {
            sendData = (IDeleteNote) fragment;
        } else {
            throw new RuntimeException(fragment + " must implement IHandleNoteAdapterChange");
        }
    }

    public void updateNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String newNoteText = notes.get(holder.getAdapterPosition()).getText();
        holder.getTextViewText().setText(newNoteText);
        holder.getButtonDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _id = notes.get(holder.getAdapterPosition()).get_id();
                sendData.deleteNote(_id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void removeNote(String _id) {
        for (int i = 0; i < notes.size(); i ++) {
            if (notes.get(i).get_id().equals(_id)) {
                notes.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void addNote(Note note) {
        notes.add(note);
        notifyDataSetChanged();
    }

    public interface IDeleteNote {
        void deleteNote(String _id);
    }
}


