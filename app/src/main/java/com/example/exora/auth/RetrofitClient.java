package com.example.exora.auth;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Gunakan IP Laptop (192.168.1.11) agar HP Fisik bisa mengakses server Node.js
    // Pastikan HP dan Laptop terhubung ke WiFi yang sama
    private static final String BASE_URL = "http://192.168.1.11:3000/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
