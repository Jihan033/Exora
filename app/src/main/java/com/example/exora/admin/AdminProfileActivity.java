package com.example.exora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.NotificationActivity;
import com.example.exora.R;
import com.example.exora.auth.LoginActivity;
import com.example.exora.auth.SessionManager;

public class AdminProfileActivity extends AppCompatActivity {

    LinearLayout btnDashboard,
            btnAgenda,
            btnClub,
            btnProfile,
            btnPrivacyProfile,
            btnManageAboutUs;
    ImageView btnAdminNotification;
    Button btnSignOut;
    TextView tvAdminName;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        sessionManager = new SessionManager(this);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);
        btnPrivacyProfile = findViewById(R.id.btnPrivacyProfile);
        btnManageAboutUs = findViewById(R.id.btnManageAboutUs);
        btnAdminNotification = findViewById(R.id.btnAdminNotification);
        
        btnSignOut = findViewById(R.id.btnSignOut);
        tvAdminName = findViewById(R.id.tvAdminName);

        if (tvAdminName != null) {
            tvAdminName.setText(sessionManager.getUserName());
        }

        // Dashboard
        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(AdminProfileActivity.this, AdminDashboardActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        // Agenda
        btnAgenda.setOnClickListener(v -> {
            startActivity(new Intent(AdminProfileActivity.this, AdminAgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        // Clubs
        btnClub.setOnClickListener(v -> {
            startActivity(new Intent(AdminProfileActivity.this, AdminClubActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        // Privacy Profile
        btnPrivacyProfile.setOnClickListener(v -> {
            startActivity(new Intent(AdminProfileActivity.this, AdminPrivacyProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        // Manage About Us
        if (btnManageAboutUs != null) {
            btnManageAboutUs.setOnClickListener(v -> {
                startActivity(new Intent(AdminProfileActivity.this, AdminManageAboutUsActivity.class));
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            });
        }

        if (btnAdminNotification != null) {
            btnAdminNotification.setOnClickListener(v -> {
                Intent intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("TARGET_TYPE", "ADMIN");
                startActivity(intent);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            });
        }

        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(v -> handleLogout());
        }
    }

    private void handleLogout() {
        Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
        sessionManager.logoutUser();
        Intent intent = new Intent(AdminProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
