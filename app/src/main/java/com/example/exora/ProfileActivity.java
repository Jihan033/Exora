package com.example.exora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends AppCompatActivity {

    // HEADER
    ImageView btnNotification;

    // VIEW ALL
    LinearLayout btnViewAll;

    // ORGANIZATION CARD
    CardView cardOsis, cardDev;

    // ATTENDANCE
    LinearLayout itemMeeting, itemHackathon;

    // SETTINGS
    LinearLayout layoutNotification,
            btnPrivacy,
            btnAppearance;

    // SWITCH
    SwitchCompat switchNotification;

    // SIGN OUT
    Button btnSignOut;

    // BOTTOM NAVIGATION
    LinearLayout btnDashboard,
            btnAgenda,
            btnClub,
            btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // HEADER
        btnNotification = findViewById(R.id.btnNotification);

        // VIEW ALL
        btnViewAll = findViewById(R.id.btnViewAll);

        // ORGANIZATION
        cardOsis = findViewById(R.id.cardOsis);
        cardDev = findViewById(R.id.cardDev);

        // ATTENDANCE
        itemMeeting = findViewById(R.id.itemMeeting);
        itemHackathon = findViewById(R.id.itemHackathon);

        // SETTINGS
        layoutNotification = findViewById(R.id.layoutNotification);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnAppearance = findViewById(R.id.btnAppearance);

        // SWITCH
        switchNotification = findViewById(R.id.switchNotification);

        // SIGN OUT
        btnSignOut = findViewById(R.id.btnSignOut);

        // BOTTOM NAVIGATION
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);

        // NOTIFICATION CLICK
        btnNotification.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "Notification Clicked",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // VIEW ALL CLICK
        btnViewAll.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "All Organizations",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // ORGANIZATION CARD CLICK
        cardOsis.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "OSIS Council",
                    Toast.LENGTH_SHORT
            ).show();

        });

        cardDev.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "Dev Society",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // ATTENDANCE CLICK
        itemMeeting.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "Monthly General Meeting",
                    Toast.LENGTH_SHORT
            ).show();

        });

        itemHackathon.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "Hackathon Planning",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // PUSH NOTIFICATION
        layoutNotification.setOnClickListener(v -> {

            switchNotification.toggle();

        });

        // SWITCH CHANGE
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                Toast.makeText(
                        ProfileActivity.this,
                        "Notifications Enabled",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        ProfileActivity.this,
                        "Notifications Disabled",
                        Toast.LENGTH_SHORT
                ).show();

            }

        });

        // PRIVACY
        btnPrivacy.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "Privacy Profile",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // APPEARANCE
        btnAppearance.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "Appearance Settings",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // SIGN OUT
        btnSignOut.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "Sign Out Success",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(new Intent(
                    ProfileActivity.this,
                    LoginActivity.class
            ));

            finish();

        });

        // DASHBOARD
        btnDashboard.setOnClickListener(v -> {

            startActivity(new Intent(
                    ProfileActivity.this,
                    DashboardActivity.class
            ));

            finish();

        });

        // AGENDA
        btnAgenda.setOnClickListener(v -> {

            startActivity(new Intent(
                    ProfileActivity.this,
                    AgendaActivity.class
            ));

            finish();

        });

        // CLUB
        btnClub.setOnClickListener(v -> {

            startActivity(new Intent(
                    ProfileActivity.this,
                    ClubActivity.class
            ));

            finish();

        });

        // PROFILE
        btnProfile.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "Already in Profile",
                    Toast.LENGTH_SHORT
            ).show();

        });

    }
}