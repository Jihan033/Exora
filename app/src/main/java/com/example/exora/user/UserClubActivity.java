package com.example.exora.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;
import com.example.exora.auth.ApiService;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;
import com.example.exora.model.ClubModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserClubActivity extends AppCompatActivity {

    private LinearLayout availableClubsContainer;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_club);

        availableClubsContainer = findViewById(R.id.availableClubsContainer);
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        findViewById(R.id.btnDashboard).setOnClickListener(v -> finish());

        fetchClubs();
    }

    private void fetchClubs() {
        apiService.getClubs().enqueue(new Callback<List<ClubModel>>() {
            @Override
            public void onResponse(Call<List<ClubModel>> call, Response<List<ClubModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayClubs(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<ClubModel>> call, Throwable t) {
                Toast.makeText(UserClubActivity.this, "Koneksi Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayClubs(List<ClubModel> clubs) {
        availableClubsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ClubModel club : clubs) {
            View card = inflater.inflate(R.layout.item_club_card, availableClubsContainer, false);
            ((TextView) card.findViewById(R.id.tvClubName)).setText(club.getName());
            ((TextView) card.findViewById(R.id.tvClubDescription)).setText(club.getDescription());
            
            card.findViewById(R.id.btnClubAction).setOnClickListener(v -> applyToClub(club));
            availableClubsContainer.addView(card);
        }
    }

    private void applyToClub(ClubModel club) {
        Map<String, Object> data = new HashMap<>();
        data.put("clubId", club.getId());
        data.put("clubName", club.getName());
        data.put("userName", sessionManager.getUserName());
        data.put("studentId", sessionManager.getStudentId());

        String token = "Bearer " + sessionManager.getToken();
        apiService.applyToClub(token, data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserClubActivity.this, "Berhasil Apply ke " + club.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserClubActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
