package com.example.djsortapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class DJDashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestsAdapter adapter;
    private List<DocumentSnapshot> requestList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dj_dashboard, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        
        recyclerView = view.findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        requestList = new ArrayList<>();
        adapter = new RequestsAdapter(requestList, new RequestsAdapter.OnRequestClickListener() {
            @Override
            public void onAcceptClick(DocumentSnapshot snapshot) {
                acceptRequest(snapshot);
            }

            @Override
            public void onDeleteClick(DocumentSnapshot snapshot) {
                deleteRequest(snapshot);
            }
        });
        recyclerView.setAdapter(adapter);

        loadRequests();

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

    private void loadRequests() {
        if (mAuth.getCurrentUser() == null) return;
        String myEmail = mAuth.getCurrentUser().getEmail();

        listenerRegistration = db.collection("requests")
                .whereEqualTo("djName", myEmail) 
                .whereEqualTo("status", "pending")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        if (getContext() != null) Toast.makeText(getContext(), "Error loading requests: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (snapshots != null) {
                        requestList.clear();
                        requestList.addAll(snapshots.getDocuments());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Found " + requestList.size() + " requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void acceptRequest(DocumentSnapshot snapshot) {
        db.collection("requests").document(snapshot.getId())
                .update("status", "accepted")
                .addOnSuccessListener(aVoid -> {
                    if (getContext() != null) Toast.makeText(getContext(), "Request Accepted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteRequest(DocumentSnapshot snapshot) {
        db.collection("requests").document(snapshot.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (getContext() != null) Toast.makeText(getContext(), "Request Deleted", Toast.LENGTH_SHORT).show();
                });
    }
}
