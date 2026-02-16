package com.example.djsortapp.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeezerService {
    @GET("search")
    Call<DeezerResponse> searchTracks(@Query("q") String query);
}
