package com.example.exora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ClubActivity extends AppCompatActivity {

    LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile, btnAllClub, btnLeadership,
    btnSports, btnArts;

    Button btnDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);
        btnDetail = findViewById(R.id.btnDetail);
        btnAllClub = findViewById(R.id.btnAllClub);
        btnLeadership = findViewById(R.id.btnLeadership);
        btnSports = findViewById(R.id.btnSports);
        btnArts = findViewById(R.id.btnArts);

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

        // Profile
        btnProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ClubActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    R.transition.slide_in_right,
                    R.transition.slide_out_left
            );

            finish();
        });

        // Club (current page)
        btnClub.setOnClickListener(v -> {
            // stay here
        });

        // VIEW DETAILS

            btnDetail.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                ClubActivity.this,
                                DetailActivity.class
                        );

                startActivity(intent);

            });

        btnAllClub.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "All Clubs",
                    Toast.LENGTH_SHORT
            ).show();

        });

        btnLeadership.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Leadership",
                    Toast.LENGTH_SHORT
            ).show();

        });

        btnSports.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Sports",
                    Toast.LENGTH_SHORT
            ).show();

        });

        btnArts.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Arts",
                    Toast.LENGTH_SHORT
            ).show();

        });

    }
}