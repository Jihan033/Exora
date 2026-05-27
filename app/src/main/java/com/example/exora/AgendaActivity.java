package com.example.exora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AgendaActivity extends AppCompatActivity {

    LinearLayout btnDashboard, btnAgenda,
            btnClub, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);

        // DASHBOARD
        btnDashboard.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AgendaActivity.this,
                            DashboardActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    R.transition.slide_in_left,
                    R.transition.slide_out_right
            );

            finish();
        });

        // CLUB
        btnClub.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AgendaActivity.this,
                            ClubActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    R.transition.slide_in_right,
                    R.transition.slide_out_left
            );

            finish();
        });

        // Profile
        btnProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AgendaActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    R.transition.slide_in_right,
                    R.transition.slide_out_left
            );

            finish();
        });

        // AGENDA
        btnAgenda.setOnClickListener(v -> {
            // stay here
        });
    }
}