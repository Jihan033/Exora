package com.example.exora.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exora.R;
import com.example.exora.auth.ApiService;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminManageMemberActivity extends AppCompatActivity {

    private LinearLayout memberListContainer;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_member);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbarManageMember);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        memberListContainer = findViewById(R.id.memberListContainer);
        fetchApplications();
    }

    private void fetchApplications() {
        String token = "Bearer " + sessionManager.getToken();
        apiService.getApplications(token).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayApplications(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(AdminManageMemberActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayApplications(List<Map<String, Object>> apps) {
        memberListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map<String, Object> app : apps) {
            View card = inflater.inflate(R.layout.item_admin_member_card, memberListContainer, false);
            
            TextView tvName = card.findViewById(R.id.tvMemberName);
            TextView tvClub = card.findViewById(R.id.tvMemberRole);
            TextView tvStatus = card.findViewById(R.id.tvTypeBadge);

            String name = (String) app.get("userName");
            String clubName = (String) app.get("clubName");
            String status = (String) app.get("status");
            int id = ((Double) app.get("id")).intValue();

            tvName.setText(name);
            tvClub.setText("Club: " + clubName);
            tvStatus.setText(status);

            if ("ACCEPTED".equals(status)) tvStatus.setBackgroundResource(R.drawable.role_selected);
            else if ("REJECTED".equals(status)) tvStatus.setBackgroundResource(R.drawable.bg_logout);

            card.setOnClickListener(v -> {
                if ("PENDING".equals(status)) showActionDialog(id, name);
            });

            memberListContainer.addView(card);
        }
    }

    private void showActionDialog(int appId, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Kelola Pendaftaran")
                .setMessage("Terima atau Tolak " + name + "?")
                .setPositiveButton("Terima", (d, w) -> updateStatus(appId, "ACCEPTED"))
                .setNegativeButton("Tolak", (d, w) -> updateStatus(appId, "REJECTED"))
                .show();
    }

    private void updateStatus(int appId, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        apiService.updateApplicationStatus("Bearer " + sessionManager.getToken(), appId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) fetchApplications();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}
