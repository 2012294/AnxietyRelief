package com.hareem.anxietyrelief.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hareem.anxietyrelief.JournalEntryModel;
import com.hareem.anxietyrelief.R;

import java.util.ArrayList;
import java.util.List;

public class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryAdapter.EntryViewHolder> {

    private List<JournalEntryModel> entryList;
    private List<JournalEntryModel> originalEntryList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public JournalEntryAdapter() {
        this.entryList = new ArrayList<>();
        this.originalEntryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        JournalEntryModel entry = entryList.get(position);

        holder.dayText.setText(entry.getDay());
        holder.dateText.setText(entry.getDate());
        holder.prompt.setText(entry.getPrompt());

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDeleteClick(holder.getAdapterPosition());
                }
            }
        });

        // Set click listener for the entire item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return entryList != null ? entryList.size() : 0;
    }

    public void setJournalEntryList(List<JournalEntryModel> entryList) {
        this.entryList = entryList;
        this.originalEntryList = new ArrayList<>(entryList);
        notifyDataSetChanged();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView dayText, dateText, prompt;
        ImageView deleteIcon;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(R.id.dayText);
            dateText = itemView.findViewById(R.id.dateText);
            prompt = itemView.findViewById(R.id.promptText);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }

    public JournalEntryModel getItem(int position) {
        return entryList.get(position);
    }

    public void removeEntry(String entryId) {
        for (int i = 0; i < entryList.size(); i++) {
            if (entryList.get(i).getId().equals(entryId)) {
                entryList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void filter(String query) {
        entryList.clear();
        if (query.isEmpty()) {
            entryList.addAll(originalEntryList);
        } else {
            query = query.toLowerCase();
            for (JournalEntryModel entry : originalEntryList) {
                if (entry.getDate().toLowerCase().contains(query) ||
                        entry.getPrompt().toLowerCase().contains(query)) {
                    entryList.add(entry);
                }
            }
        }
        notifyDataSetChanged();
    }
}
