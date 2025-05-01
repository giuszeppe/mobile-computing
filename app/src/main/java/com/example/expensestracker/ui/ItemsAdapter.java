package com.example.expensestracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensestracker.R;

import java.util.List;

public class ItemsAdapter extends
        RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private List<String> items;
    public ItemsAdapter(List<String> items) {
        this.items = items;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.layout_item, parent,
                false);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        String item = items.get(position);
        TextView primaryTextView = holder.primaryTextView;
        primaryTextView.setText(item);
        TextView secondaryTextView = holder.secondaryTextView;
        secondaryTextView.setText("Unread");
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImageView;
        public TextView primaryTextView;
        public TextView secondaryTextView;
        public ViewHolder(final View itemView) {
            super(itemView);
            iconImageView = (ImageView)
                    itemView.findViewById(R.id.mtrl_list_item_icon);
            primaryTextView = (TextView)
                    itemView.findViewById(R.id.item_primary_text);
            secondaryTextView = (TextView)
                    itemView.findViewById(R.id.item_secondary_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemOnClick();
                }
            });
        }
        public void itemOnClick() {
            if(secondaryTextView.getText().equals("Unread")){
                secondaryTextView.setText("Read");

                iconImageView.setImageResource(R.drawable.ic_notifications_black_24dp);
            } else {
                secondaryTextView.setText("Unread");

                iconImageView.setImageResource(R.drawable.ic_notifications_purple_24dp);
            }
        }
    }
}