package com.example.exora.admin;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;

public class DashboardActivity extends AppCompatActivity {

    LinearLayout btnDashboard, btnAgenda,
            btnClub, btnProfile;
    TextView btnViewCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnDashboard =
                findViewById(R.id.btnDashboard);

        btnAgenda =
                findViewById(R.id.btnAgenda);

        btnClub =
                findViewById(R.id.btnClub);

        btnProfile =
                findViewById(R.id.btnProfile);

        btnViewCalendar =
                findViewById(R.id.btnViewCalendar);

        btnDashboard.setOnClickListener(v ->
                Toast.makeText(this,
                        "Dashboard",
                        Toast.LENGTH_SHORT).show());

        btnAgenda.setOnClickListener(v -> {
            Intent intent =
                    new Intent(DashboardActivity.this,
                            AgendaActivity.class);

            startActivity(intent);

            overridePendingTransition(
                    R.transition.slide_in_right,
                    R.transition.slide_out_left
            );
        });

        btnClub.setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            DashboardActivity.this,
                            ClubActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    R.transition.slide_in_right,
                    R.transition.slide_out_left
            );
        });

        btnProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DashboardActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    R.transition.slide_in_right,
                    R.transition.slide_out_left
            );

        });
        btnViewCalendar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DashboardActivity.this,
                            AgendaActivity.class
                    );

            startActivity(intent);

        });
    }
}