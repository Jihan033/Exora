package com.example.exora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.exora.NotificationActivity;
import com.example.exora.R;
import com.example.exora.auth.ApiService;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;
import com.example.exora.model.ClubModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminClubActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private CardView btnManageMembers, btnAddClub;
    private LinearLayout clubListContainer;
    private TextView tvTotalClubsCount;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_club);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        // UI References
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);
        btnManageMembers = findViewById(R.id.btnManageMembers);
        btnAddClub = findViewById(R.id.btnRecruitment); 
        clubListContainer = findViewById(R.id.clubMemberListContainer);
        tvTotalClubsCount = findViewById(R.id.tvTotalMembersCount);

        // Perbaikan error symbol not found: Langsung cari ID di layout utama
        TextView tvRecruitmentText = findViewById(R.id.tvRecruitmentText);
        if (tvRecruitmentText != null) {
            tvRecruitmentText.setText("Add New Club");
        }

        setupNavigation();

        btnManageMembers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminClubActivity.this, AdminManageMemberActivity.class);
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnAddClub.setOnClickListener(v -> showAddClubDialog());
        
        ImageView btnAdminNotif = findViewById(R.id.btnAdminNotification);
        if (btnAdminNotif != null) {
            btnAdminNotif.setOnClickListener(v -> {
                Intent intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("TARGET_TYPE", "ADMIN");
                startActivity(intent);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                Toast.makeText(AdminClubActivity.this, "Gagal mengambil data server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayClubs(List<ClubModel> clubs) {
        clubListContainer.removeAllViews();
        tvTotalClubsCount.setText(String.valueOf(clubs.size()));
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ClubModel club : clubs) {
            View itemView = inflater.inflate(R.layout.item_admin_member_card, clubListContainer, false);
            TextView tvName = itemView.findViewById(R.id.tvMemberName);
            TextView tvCategory = itemView.findViewById(R.id.tvMemberRole);
            TextView tvBadge = itemView.findViewById(R.id.tvTypeBadge);

            tvName.setText(club.getName());
            tvCategory.setText(club.getCategory());
            tvBadge.setText("DELETE");
            tvBadge.setBackgroundResource(R.drawable.bg_logout);

            // Klik badge untuk hapus
            tvBadge.setOnClickListener(v -> showDeleteConfirm(club));
            
            // Klik item untuk edit
            itemView.setOnClickListener(v -> showEditClubDialog(club));

            clubListContainer.addView(itemView);
        }
    }

    private void showDeleteConfirm(ClubModel club) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Club")
                .setMessage("Yakin ingin menghapus " + club.getName() + "?")
                .setPositiveButton("Hapus", (d, w) -> deleteClub(club.getId()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteClub(int clubId) {
        String token = "Bearer " + sessionManager.getToken();
        apiService.deleteClub(token, clubId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminClubActivity.this, "Club dihapus", Toast.LENGTH_SHORT).show();
                    fetchClubs();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void showAddClubDialog() {
        showClubFormDialog(null);
    }
    
    private void showEditClubDialog(ClubModel club) {
        showClubFormDialog(club);
    }

    private void showClubFormDialog(ClubModel club) {
        android.widget.EditText etName = new android.widget.EditText(this);
        etName.setHint("Nama Club");
        if (club != null) etName.setText(club.getName());
        
        new AlertDialog.Builder(this)
                .setTitle(club == null ? "Add New Club" : "Edit Club")
                .setView(etName)
                .setPositiveButton("Simpan", (d, w) -> {
                    String name = etName.getText().toString();
                    if (!name.isEmpty()) {
                        if (club == null) createClub(name);
                        else updateClub(club.getId(), name);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void createClub(String name) {
        ClubModel newClub = new ClubModel(name, "General", "Organisasi Mahasiswa");
        String token = "Bearer " + sessionManager.getToken();
        apiService.createClub(token, newClub).enqueue(new Callback<ClubModel>() {
            @Override
            public void onResponse(Call<ClubModel> call, Response<ClubModel> response) {
                if (response.isSuccessful()) fetchClubs();
            }
            @Override
            public void onFailure(Call<ClubModel> call, Throwable t) {}
        });
    }
    
    private void updateClub(int id, String name) {
        ClubModel updatedClub = new ClubModel(id, name, "General", "Info diperbarui");
        String token = "Bearer " + sessionManager.getToken();
        apiService.updateClub(token, id, updatedClub).enqueue(new Callback<ClubModel>() {
            @Override
            public void onResponse(Call<ClubModel> call, Response<ClubModel> response) {
                if (response.isSuccessful()) fetchClubs();
            }
            @Override
            public void onFailure(Call<ClubModel> call, Throwable t) {}
        });
    }

    private void setupNavigation() {
        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });
        btnAgenda.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            finish();
        });
    }
}
