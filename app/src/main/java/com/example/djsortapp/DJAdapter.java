package com.example.djsortapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class DJAdapter extends RecyclerView.Adapter<DJAdapter.DJViewHolder> {

    private List<DJ> djList;

    public DJAdapter(List<DJ> djList) {
        this.djList = djList;
    }

    @NonNull
    @Override
    public DJViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dj, parent, false);
        return new DJViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DJViewHolder holder, int position) {
        DJ dj = djList.get(position);
        holder.name.setText(dj.getName());
        holder.genre.setText(dj.getGenre());
        
        if (dj.getAddress() != null && !dj.getAddress().isEmpty()) {
             holder.location.setText(dj.getAddress());
        } else if (dj.getLatitude() != 0 && dj.getLongitude() != 0) {
            holder.location.setText(String.format("Loc: %.4f, %.4f", dj.getLatitude(), dj.getLongitude()));
        } else {
            holder.location.setText("Location: Unknown");
        }

        // Use Glide to load image (placeholder for now if empty)
        Glide.with(holder.itemView.getContext())
             .load(dj.getImageUrl())
             .placeholder(R.mipmap.ic_launcher)
             .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return djList.size();
    }

    public static class DJViewHolder extends RecyclerView.ViewHolder {
        TextView name, genre, location;
        ImageView image;

        public DJViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvDJName);
            genre = itemView.findViewById(R.id.tvDJGenre);
            location = itemView.findViewById(R.id.tvDJLocation);
            image = itemView.findViewById(R.id.ivDJImage);
        }
    }
}
