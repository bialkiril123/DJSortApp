package com.example.djsortapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.djsortapp.network.DeezerTrack;
import java.util.List;

public class TrackSearchAdapter extends RecyclerView.Adapter<TrackSearchAdapter.TrackViewHolder> {

    private List<DeezerTrack> trackList;
    private OnTrackClickListener listener;

    public interface OnTrackClickListener {
        void onTrackClick(DeezerTrack track);
    }

    public TrackSearchAdapter(List<DeezerTrack> trackList, OnTrackClickListener listener) {
        this.trackList = trackList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_search, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        DeezerTrack track = trackList.get(position);
        holder.tvTitle.setText(track.getTitle());
        holder.tvArtist.setText(track.getArtist().getName());
        
        Glide.with(holder.itemView.getContext())
            .load(track.getAlbum().getCoverMedium())
            .into(holder.imgAlbumArt);
            
        holder.itemView.setOnClickListener(v -> listener.onTrackClick(track));
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist;
        ImageView imgAlbumArt;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTrackTitle);
            tvArtist = itemView.findViewById(R.id.tvArtistName);
            imgAlbumArt = itemView.findViewById(R.id.imgAlbumArt);
        }
    }
}
