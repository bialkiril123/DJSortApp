package com.example.djsortapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DJListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DJAdapter adapter;
    private List<DJ> djList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dj_list, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerViewDJs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        djList = new ArrayList<>();
        
        // 1. ADD MOCK DATA (As requested)
        loadMockData();

        adapter = new DJAdapter(djList);
        recyclerView.setAdapter(adapter);
        
        // 2. ALSO LOAD REAL DATA (To show location updates)
        loadRealDJs();
        
        return view;
    }

    private void loadMockData() {
        djList.add(new DJ("David Guetta", "House", "https://i.scdn.co/image/ab6761610000e5eb1d5f5f5f5f5f5f5f5f5f5f5f", "david@guetta.com", 0, 0, ""));
        djList.add(new DJ("DJ Tiesto", "Trance", "https://i.scdn.co/image/ab6761610000e5eb1d5f5f5f5f5f5f5f5f5f5f5f", "tiesto@music.com", 0, 0, ""));
        djList.add(new DJ("Martin Garrix", "Electro", "https://i.scdn.co/image/ab6761610000e5eb1d5f5f5f5f5f5f5f5f5f5f5f", "martin@garrix.com", 0, 0, ""));
        djList.add(new DJ("Calvin Harris", "Pop/House", "https://i.scdn.co/image/ab6761610000e5eb1d5f5f5f5f5f5f5f5f5f5f5f", "calvin@harris.com", 0, 0, ""));
        djList.add(new DJ("Armin van Buuren", "Trance", "", "armin@asot.com", 0, 0, ""));
        djList.add(new DJ("Marshmello", "Trap", "", "mello@marsh.com", 0, 0, ""));
        djList.add(new DJ("Steve Aoki", "EDM", "", "steve@aoki.com", 0, 0, ""));
        djList.add(new DJ("Zedd", "Electro House", "", "zedd@music.com", 0, 0, ""));
        djList.add(new DJ("Skrillex", "Dubstep", "", "skrillex@owsla.com", 0, 0, ""));
        djList.add(new DJ("Hardwell", "Big Room", "", "hardwell@revealed.com", 0, 0, ""));
    }

    private void loadRealDJs() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        
        db.collection("users")
                .whereEqualTo("role", "DJ")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;

                    if (snapshots != null) {
                        // Reset to base mock data first
                        djList.clear();
                        loadMockData(); 
                        
                        for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                            DJ realtimeDJ = doc.toObject(DJ.class);
                            if (realtimeDJ != null && realtimeDJ.getEmail() != null) {
                                boolean found = false;
                                // Try to find and update existing mock DJ
                                for (int i = 0; i < djList.size(); i++) {
                                    if (djList.get(i).getEmail() != null && djList.get(i).getEmail().equalsIgnoreCase(realtimeDJ.getEmail())) {
                                        // Update location info ONLY (keep name/image from mock if desired, or overwrite)
                                        // Here we overwrite with real data but ensure Name is valid
                                        String originalName = djList.get(i).getName();
                                        String originalImage = djList.get(i).getImageUrl();
                                        
                                        // Use realtime data, but fallback to mock name if realtime name is missing/default
                                        if (realtimeDJ.getName() == null || realtimeDJ.getName().isEmpty()) {
                                            // Create a new DJ object mixing Mock Name + Real Location
                                            DJ updatedDJ = new DJ(
                                                originalName, 
                                                djList.get(i).getGenre(), 
                                                originalImage, 
                                                realtimeDJ.getEmail(), 
                                                realtimeDJ.getLatitude(), 
                                                realtimeDJ.getLongitude(), 
                                                realtimeDJ.getAddress()
                                            );
                                            djList.set(i, updatedDJ);
                                        } else {
                                            djList.set(i, realtimeDJ);
                                        }
                                        found = true;
                                        break;
                                    }
                                }
                                
                                // If not found in mock list, add as new entry
                                if (!found) {
                                    djList.add(realtimeDJ);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
