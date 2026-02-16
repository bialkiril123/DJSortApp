package com.example.djsortapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class DJQueueFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestsAdapter adapter;
    private List<DocumentSnapshot> requestList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dj_dashboard, container, false); // Reuse layout

        TextView title = view.findViewById(R.id.tvDashboardTitle);
        if (title != null) {
            title.setText("Waiting List (Queue)");
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        
        recyclerView = view.findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        requestList = new ArrayList<>();
        adapter = new RequestsAdapter(requestList, new RequestsAdapter.OnRequestClickListener() {
            @Override
            public void onAcceptClick(DocumentSnapshot snapshot) {
                // Should not happen in Queue
            }

            @Override
            public void onDeleteClick(DocumentSnapshot snapshot) {
                deleteRequest(snapshot);
            }
        });
        recyclerView.setAdapter(adapter);

        loadQueue();

        return view;
    }

    private com.google.firebase.firestore.ListenerRegistration listenerRegistration;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void loadQueue() {
        if (mAuth.getCurrentUser() == null) return;
        String myEmail = mAuth.getCurrentUser().getEmail();

        // Show Accepted requests, sorted by time (oldest first = FIFO queue)
        listenerRegistration = db.collection("requests")
                .whereEqualTo("status", "accepted")
                .whereEqualTo("djName", myEmail) 
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        if (getContext() != null) Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (snapshots != null) {
                        requestList.clear();
                        requestList.addAll(snapshots.getDocuments());
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void deleteRequest(DocumentSnapshot snapshot) {
        db.collection("requests").document(snapshot.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (getContext() != null) Toast.makeText(getContext(), "Removed from queue", Toast.LENGTH_SHORT).show();
                });
    }
}
