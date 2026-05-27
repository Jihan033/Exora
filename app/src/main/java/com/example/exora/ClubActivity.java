package com.example.exora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ClubActivity extends AppCompatActivity {

    LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);

        // Dashboard
        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ClubActivity.this,
                    DashboardActivity.class
            );
            startActivity(intent);
            finish();
        });

        // Agenda
        btnAgenda.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ClubActivity.this,
                            AgendaActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    R.transition.slide_in_left,
                    R.transition.slide_out_right
            );

            finish();
        });

        // Club (current page)
        btnClub.setOnClickListener(v -> {
            // stay here
        });
    }
}