package com.example.exora.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;
import com.example.exora.auth.ApiService;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.model.AboutUsModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutUsActivity extends AppCompatActivity {

    private TextView tvAboutUsContent;
    private ImageView btnBack;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        tvAboutUsContent = findViewById(R.id.tvAboutUsContent);
        btnBack = findViewById(R.id.btnBack);

        apiService = RetrofitClient.getApiService();

        btnBack.setOnClickListener(v -> finish());

        fetchAboutUs();
    }

    private void fetchAboutUs() {
        apiService.getAboutUs().enqueue(new Callback<AboutUsModel>() {
            @Override
            public void onResponse(Call<AboutUsModel> call, Response<AboutUsModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvAboutUsContent.setText(response.body().getContent());
                } else {
                    tvAboutUsContent.setText("Gagal mengambil informasi About Us.");
                }
            }

            @Override
            public void onFailure(Call<AboutUsModel> call, Throwable t) {
                tvAboutUsContent.setText("Error koneksi: " + t.getMessage());
                Toast.makeText(AboutUsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
