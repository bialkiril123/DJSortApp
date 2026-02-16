package com.example.djsortapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    private List<DocumentSnapshot> requestList;
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onAcceptClick(DocumentSnapshot snapshot);
        void onDeleteClick(DocumentSnapshot snapshot);
    }

    public RequestsAdapter(List<DocumentSnapshot> requestList, OnRequestClickListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        DocumentSnapshot snapshot = requestList.get(position);
        Request request = snapshot.toObject(Request.class);

        if (request != null) {
            holder.tvSong.setText(request.getSongName());
            holder.tvArtist.setText(request.getArtistName());
            
            if (request.getAlbumArtUrl() != null && !request.getAlbumArtUrl().isEmpty()) {
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(request.getAlbumArtUrl())
                        .into(holder.imgAlbumArt);
            } else {
                 holder.imgAlbumArt.setImageResource(R.drawable.ic_launcher_background);
            }
            
            if ("accepted".equals(request.getStatus())) {
                holder.btnAccept.setVisibility(View.GONE); // Hide Accept in Queue
                holder.btnDelete.setText("Remove"); // Rename for Queue
            } else {
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText("Accept");
                holder.btnAccept.setOnClickListener(v -> listener.onAcceptClick(snapshot));
                holder.btnDelete.setText("Delete");
            }
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(snapshot));
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvSong, tvArtist;
        Button btnAccept, btnDelete;
        ImageView imgAlbumArt;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSong = itemView.findViewById(R.id.tvSongName);
            tvArtist = itemView.findViewById(R.id.tvArtistName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imgAlbumArt = itemView.findViewById(R.id.imgAlbumArtRequest);
        }
    }
}
