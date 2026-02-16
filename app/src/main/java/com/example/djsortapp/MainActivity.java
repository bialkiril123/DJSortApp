package com.example.djsortapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isDJ = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        // Check User Role
        checkUserRole(bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_dj_list) {
                // If DJ, show Dashboard instead of DJ List? Or maybe add a new menu item?
                // For simplicity, we'll override the first tab for DJ
                if (isDJ) {
                    selectedFragment = new DJDashboardFragment();
                } else {
                    selectedFragment = new DJListFragment();
                }
            } else if (itemId == R.id.nav_song_request) {
                if (isDJ) {
                    selectedFragment = new DJQueueFragment();
                } else {
                    selectedFragment = new SongRequestFragment();
                }
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    private void checkUserRole(BottomNavigationView bottomNav) {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("DJ".equals(role)) {
                            isDJ = true;
                            // Update UI for DJ
                            bottomNav.getMenu().findItem(R.id.nav_dj_list).setTitle("Dashboard");
                            bottomNav.getMenu().findItem(R.id.nav_dj_list).setIcon(android.R.drawable.ic_dialog_email); // Requests icon
                            
                            bottomNav.getMenu().findItem(R.id.nav_song_request).setTitle("Queue");
                            bottomNav.getMenu().findItem(R.id.nav_song_request).setIcon(android.R.drawable.ic_menu_sort_by_size); // List icon
                            
                            // Load Dashboard immediately
                            getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new DJDashboardFragment())
                                .commit();
                        } else {
                            // Default load for User
                            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                                getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new DJListFragment())
                                    .commit();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Fail silently or warn? Warn for now to help debug
                    // Toast.makeText(MainActivity.this, "Failed to get user role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}