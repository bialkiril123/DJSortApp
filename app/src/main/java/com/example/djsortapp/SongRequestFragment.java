package com.example.djsortapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.djsortapp.network.ApiClient;
import com.example.djsortapp.network.DeezerResponse;
import com.example.djsortapp.network.DeezerService;
import com.example.djsortapp.network.DeezerTrack;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

public class SongRequestFragment extends Fragment {

    private EditText etSongName, etArtistName, etSearch;
    private Button btnSubmit, btnSearch;
    private RecyclerView rvSearchResults;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    
    private String selectedAlbumArtUrl = "";
    private String selectedPreviewUrl = "";

    private android.widget.Spinner spinnerDJs;
    private java.util.Map<String, String> djMap; // Name -> Email

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_request, container, false);

        etSongName = view.findViewById(R.id.etSongName);
        etArtistName = view.findViewById(R.id.etArtistName);
        etSearch = view.findViewById(R.id.etSearch);
        btnSubmit = view.findViewById(R.id.btnSubmitRequest);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        spinnerDJs = view.findViewById(R.id.spinnerDJs);
        
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupDJSpinner();
        
        btnSearch.setOnClickListener(v -> searchSong(etSearch.getText().toString()));

        btnSubmit.setOnClickListener(v -> {
            String song = etSongName.getText().toString();
            String artist = etArtistName.getText().toString();
            String selectedDjName = spinnerDJs.getSelectedItem().toString();
            String targetDjEmail = djMap.get(selectedDjName);
            
            if (!song.isEmpty() && !artist.isEmpty() && targetDjEmail != null) {
                submitRequest(song, artist, targetDjEmail);
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setupDJSpinner() {
        djMap = new java.util.LinkedHashMap<>();
        djMap.put("David Guetta", "david@guetta.com");
        djMap.put("DJ Tiesto", "tiesto@music.com");
        djMap.put("Martin Garrix", "martin@garrix.com");
        djMap.put("Calvin Harris", "calvin@harris.com");
        djMap.put("Armin van Buuren", "armin@asot.com");
        djMap.put("Marshmello", "mello@marsh.com");
        djMap.put("Steve Aoki", "steve@aoki.com");
        djMap.put("Zedd", "zedd@music.com");
        djMap.put("Skrillex", "skrillex@owsla.com");
        djMap.put("Hardwell", "hardwell@revealed.com");

        List<String> djNames = new java.util.ArrayList<>(djMap.keySet());
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, djNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDJs.setAdapter(adapter);
    }

    private void searchSong(String query) {
        if (query.isEmpty()) return;
        
        Toast.makeText(getContext(), "Searching...", Toast.LENGTH_SHORT).show();
        
        com.example.djsortapp.network.DeezerService service = com.example.djsortapp.network.ApiClient.getClient().create(com.example.djsortapp.network.DeezerService.class);
        service.searchTracks(query).enqueue(new retrofit2.Callback<com.example.djsortapp.network.DeezerResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.djsortapp.network.DeezerResponse> call, retrofit2.Response<com.example.djsortapp.network.DeezerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.djsortapp.network.DeezerTrack> tracks = response.body().getData();
                    showSearchResults(tracks);
                } else {
                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.djsortapp.network.DeezerResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSearchResults(List<com.example.djsortapp.network.DeezerTrack> tracks) {
        rvSearchResults.setVisibility(View.VISIBLE);
        TrackSearchAdapter adapter = new TrackSearchAdapter(tracks, track -> {
            etSongName.setText(track.getTitle());
            etArtistName.setText(track.getArtist().getName());
            selectedAlbumArtUrl = track.getAlbum().getCoverMedium();
            selectedPreviewUrl = track.getPreview();
            
            rvSearchResults.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Selected: " + track.getTitle(), Toast.LENGTH_SHORT).show();
        });
        rvSearchResults.setAdapter(adapter);
    }

    private void submitRequest(String song, String artist, String targetDj) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request(
                song, 
                artist, 
                mAuth.getCurrentUser().getUid(), 
                targetDj, 
                "pending", 
                Timestamp.now()
        );
        request.setAlbumArtUrl(selectedAlbumArtUrl);
        request.setPreviewUrl(selectedPreviewUrl);

        db.collection("requests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Request sent to " + targetDj + "!", Toast.LENGTH_SHORT).show();
                        etSongName.setText("");
                        etArtistName.setText("");
                        etSearch.setText("");
                        selectedAlbumArtUrl = "";
                        selectedPreviewUrl = "";
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to send: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
