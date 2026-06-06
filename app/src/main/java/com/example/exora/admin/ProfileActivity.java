package com.example.exora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout btnDashboard,
            btnAgenda,
            btnClub,
            btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);

        // Dashboard
        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(
                    ProfileActivity.this,
                    DashboardActivity.class
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
                    ProfileActivity.this,
                    AgendaActivity.class
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
                    ProfileActivity.this,
                    ClubActivity.class
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
    }
}
