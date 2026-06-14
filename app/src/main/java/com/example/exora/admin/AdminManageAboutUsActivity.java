package com.example.exora.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;
import com.example.exora.auth.ApiService;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;
import com.example.exora.model.AboutUsModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminManageAboutUsActivity extends AppCompatActivity {

    private EditText etAboutUsContent;
    private Button btnSave;
    private ImageView btnBack;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_about_us);

        etAboutUsContent = findViewById(R.id.etAboutUsContent);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        btnBack.setOnClickListener(v -> finish());

        fetchAboutUs();

        btnSave.setOnClickListener(v -> updateAboutUs());
    }

    private void fetchAboutUs() {
        apiService.getAboutUs().enqueue(new Callback<AboutUsModel>() {
            @Override
            public void onResponse(Call<AboutUsModel> call, Response<AboutUsModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    etAboutUsContent.setText(response.body().getContent());
                } else {
                    Toast.makeText(AdminManageAboutUsActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AboutUsModel> call, Throwable t) {
                Toast.makeText(AdminManageAboutUsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAboutUs() {
        String content = etAboutUsContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Konten tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AboutUsModel aboutUs = new AboutUsModel(content);
        String token = "Bearer " + sessionManager.getToken();

        apiService.updateAboutUs(token, aboutUs).enqueue(new Callback<AboutUsModel>() {
            @Override
            public void onResponse(Call<AboutUsModel> call, Response<AboutUsModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminManageAboutUsActivity.this, "Berhasil memperbarui About Us", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminManageAboutUsActivity.this, "Gagal memperbarui", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AboutUsModel> call, Throwable t) {
                Toast.makeText(AdminManageAboutUsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
