package com.example.exora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.NotificationActivity;
import com.example.exora.R;

public class AdminProfileActivity extends AppCompatActivity {

    LinearLayout btnDashboard,
            btnAgenda,
            btnClub,
            btnProfile,
            btnPrivacyProfile;
    ImageView btnAdminNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);
        btnPrivacyProfile = findViewById(R.id.btnPrivacyProfile);
        btnAdminNotification = findViewById(R.id.btnAdminNotification);

        // Dashboard
        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminProfileActivity.this,
                    AdminDashboardActivity.class
            ));

            overridePendingTransition(
                    R.transition.slide_in_left,
                    R.transition.slide_out_right
            );

            finish();
        });

        // Agenda
        btnAgenda.setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminProfileActivity.this,
                    AdminAgendaActivity.class
            ));

            overridePendingTransition(
                    R.transition.slide_in_left,
                    R.transition.slide_out_right
            );

            finish();
        });

        // Clubs
        btnClub.setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminProfileActivity.this,
                    AdminClubActivity.class
            ));

            overridePendingTransition(
                    R.transition.slide_in_left,
                    R.transition.slide_out_right
            );

            finish();
        });

        // Profile (stay here)
        btnProfile.setOnClickListener(v -> {
        });

        // Privacy Profile
        btnPrivacyProfile.setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminProfileActivity.this,
                    AdminPrivacyProfileActivity.class
            ));

            overridePendingTransition(
                    R.transition.slide_in_right,
                    R.transition.slide_out_left
            );
        });

        if (btnAdminNotification != null) {
            btnAdminNotification.setOnClickListener(v -> {
                Intent intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("TARGET_TYPE", "ADMIN");
                startActivity(intent);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            });
        }
    }
}
