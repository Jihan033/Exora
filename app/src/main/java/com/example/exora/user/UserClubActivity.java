package com.example.exora.user;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.NotificationActivity;
import com.example.exora.R;
import com.example.exora.auth.ApiService;
import com.example.exora.auth.ClubApplyRequest;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;
import com.example.exora.database.DatabaseHelper;
import com.example.exora.model.ClubModel;
import com.example.exora.model.RecruitmentModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserClubActivity extends AppCompatActivity {

    private static final String TAG = "UserClubActivity";
    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private LinearLayout myClubsContainer, availableClubsContainer;
    private TextView tvMyClubsTitle;
    private ImageView imgHeaderProfile;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ApiService apiService;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_club);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserName = sessionManager.getUserName();
        apiService = RetrofitClient.getApiService();

        // UI References
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);
        imgHeaderProfile = findViewById(R.id.imgHeaderProfile);
        
        myClubsContainer = findViewById(R.id.myClubsContainer);
        availableClubsContainer = findViewById(R.id.availableClubsContainer);
        tvMyClubsTitle = findViewById(R.id.tvMyClubsTitle);

        setupNavigation();
        
        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "USER");
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHeaderData();
        fetchClubsAndRecruitments();
    }

    private void loadHeaderData() {
        Cursor cursor = dbHelper.getUser(currentUserName);
        if (cursor != null && cursor.moveToFirst()) {
            String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            if (imageUriStr != null && !imageUriStr.isEmpty()) {
                if (imgHeaderProfile != null) {
                    imgHeaderProfile.setImageURI(Uri.parse(imageUriStr));
                }
            }
            cursor.close();
        }
    }

    private void fetchClubsAndRecruitments() {
        String token = "Bearer " + sessionManager.getUserName();
        
        // Fetch My Clubs
        apiService.getMyClubs(token).enqueue(new Callback<List<ClubModel>>() {
            @Override
            public void onResponse(Call<List<ClubModel>> call, Response<List<ClubModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayMyClubs(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ClubModel>> call, Throwable t) {
                Log.e(TAG, "Error fetching my clubs", t);
            }
        });

        // Fetch Available Recruitments
        apiService.getRecruitments().enqueue(new Callback<List<RecruitmentModel>>() {
            @Override
            public void onResponse(Call<List<RecruitmentModel>> call, Response<List<RecruitmentModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayAvailableClubs(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<RecruitmentModel>> call, Throwable t) {
                Log.e(TAG, "Error fetching recruitments", t);
            }
        });
    }

    private void displayMyClubs(List<ClubModel> clubs) {
        myClubsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        
        if (!clubs.isEmpty()) {
            tvMyClubsTitle.setVisibility(View.VISIBLE);
            for (ClubModel club : clubs) {
                View clubCard = inflater.inflate(R.layout.item_club_card, myClubsContainer, false);
                ((TextView) clubCard.findViewById(R.id.tvClubName)).setText(club.getName());
                ((TextView) clubCard.findViewById(R.id.tvClubCategory)).setText(club.getCategory());
                ((TextView) clubCard.findViewById(R.id.tvClubStatus)).setText("ACTIVE");
                ((TextView) clubCard.findViewById(R.id.tvClubDescription)).setText(club.getDescription());
                clubCard.findViewById(R.id.tvClubDeadline).setVisibility(View.GONE);
                
                Button btnAction = clubCard.findViewById(R.id.btnClubAction);
                btnAction.setText("View Dashboard");
                btnAction.setOnClickListener(v -> {
                    Intent intent = new Intent(this, UserClubDashboardActivity.class);
                    intent.putExtra("CLUB_NAME", club.getName());
                    startActivity(intent);
                });
                myClubsContainer.addView(clubCard);
            }
        } else {
            tvMyClubsTitle.setVisibility(View.GONE);
        }
    }

    private void displayAvailableClubs(List<RecruitmentModel> recruitments) {
        availableClubsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (RecruitmentModel recruitment : recruitments) {
            View clubCard = inflater.inflate(R.layout.item_club_card, availableClubsContainer, false);
            ((TextView) clubCard.findViewById(R.id.tvClubName)).setText(recruitment.getClubName());
            ((TextView) clubCard.findViewById(R.id.tvClubDescription)).setText(recruitment.getDescription());
            ((TextView) clubCard.findViewById(R.id.tvClubDeadline)).setText("Deadline: " + recruitment.getDeadline());
            
            Button btnApply = clubCard.findViewById(R.id.btnClubAction);
            btnApply.setText("Apply Now");
            btnApply.setOnClickListener(v -> applyToClub(recruitment.getClubName()));

            availableClubsContainer.addView(clubCard);
        }
    }

    private void applyToClub(String clubName) {
        String token = "Bearer " + sessionManager.getUserName();
        apiService.applyToClub(token, new ClubApplyRequest(clubName)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserClubActivity.this, "Application submitted for " + clubName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserClubActivity.this, "Failed to submit application", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserClubActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigation() {
        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, UserDashboardActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        btnAgenda.setOnClickListener(v -> {
            startActivity(new Intent(this, UserAgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            finish();
        });
    }
}
