package com.example.djsortapp.network;

import com.example.djsortapp.DJ;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("djs")
    Call<List<DJ>> getDJs();
}
