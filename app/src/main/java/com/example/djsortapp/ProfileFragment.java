package com.example.djsortapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment implements android.location.LocationListener {

    private TextView tvName, tvEmail;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private com.google.firebase.firestore.FirebaseFirestore db;

    private Button btnUpdateLocation, btnViewMap;
    private TextView tvLocationStatus;
    
    private android.location.LocationManager locationManager;
    private String provider;
    
    // Store current location for Map
    private double currentLat = 0;
    private double currentLon = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnUpdateLocation = view.findViewById(R.id.btnUpdateLocation);
        btnViewMap = view.findViewById(R.id.btnViewMap);
        tvLocationStatus = view.findViewById(R.id.tvLocationStatus);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
            tvName.setText("User ID: " + user.getUid().substring(0, 5));
        }

        locationManager = (android.location.LocationManager) requireActivity().getSystemService(android.content.Context.LOCATION_SERVICE);
        
        btnUpdateLocation.setOnClickListener(v -> updateLocation());
        
        btnViewMap.setOnClickListener(v -> {
            if (currentLat != 0 && currentLon != 0 && getActivity() != null) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("lat", String.valueOf(currentLat));
                intent.putExtra("lon", String.valueOf(currentLon));
                startActivity(intent);
            } else {
                android.widget.Toast.makeText(getContext(), "Location not found yet", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void updateLocation() {
        if (androidx.core.app.ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED && 
            androidx.core.app.ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        android.location.Criteria criteria = new android.location.Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        
        if (provider != null) {
            android.location.Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                onLocationChanged(location);
            } else {
                locationManager.requestLocationUpdates(provider, 400, 1, this);
                tvLocationStatus.setText("Searching for GPS...");
            }
        } else {
            setDefaultLocation();
        }
    }

    private void setDefaultLocation() {
        tvLocationStatus.setText("GPS Unavailable. Using Default: Israel");
        // Coordinates for Tel Aviv, Israel
        currentLat = 32.0853;
        currentLon = 34.7818;
        
        btnViewMap.setVisibility(View.VISIBLE);
        uploadLocation(currentLat, currentLon, "Tel Aviv, Israel (Default)");
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        
        btnViewMap.setVisibility(View.VISIBLE);

        // Get detailed address and update UI/Firestore
        getFull(String.valueOf(currentLat), String.valueOf(currentLon));
        
        locationManager.removeUpdates(this);
    }

    private void getFull(String latStr, String lonStr) {
        new Thread(() -> {
            try {
                android.location.Geocoder geocoder = new android.location.Geocoder(getContext(), java.util.Locale.getDefault());
                java.util.List<android.location.Address> addressList = geocoder.getFromLocation(Double.parseDouble(latStr), Double.parseDouble(lonStr), 1);
                
                if (addressList != null && !addressList.isEmpty()) {
                    android.location.Address addr = addressList.get(0);
                    
                    String address = addr.getAddressLine(0);
                    String city = addr.getLocality();
                    String country = addr.getCountryName();
                    
                    String fullText = address;
                    
                    if (city != null && country != null) {
                        fullText = city + ", " + country; 
                    }
                    
                    String displayAddress = fullText;

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvLocationStatus.setText("Current: " + displayAddress);
                            uploadLocation(Double.parseDouble(latStr), Double.parseDouble(lonStr), displayAddress);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void uploadLocation(double lat, double lon, String address) {
        if (mAuth.getCurrentUser() != null) {
            java.util.Map<String, Object> locationData = new java.util.HashMap<>();
            locationData.put("latitude", lat);
            locationData.put("longitude", lon);
            locationData.put("address", address);
            
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .update(locationData)
                    .addOnSuccessListener(aVoid -> {
                        // Toast or log if needed
                    });
        }
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override public void onProviderEnabled(@NonNull String provider) {}
    @Override public void onProviderDisabled(@NonNull String provider) {
        setDefaultLocation();
    }
}
