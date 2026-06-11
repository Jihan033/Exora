package com.example.exora.user;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.exora.auth.SessionManager;
import com.example.exora.database.DatabaseHelper;
import com.example.exora.model.ClubModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserClubActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private LinearLayout myClubsContainer, availableClubsContainer;
    private TextView tvMyClubsTitle;
    private ImageView imgHeaderProfile;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String currentUserName;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_club);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserName = sessionManager.getUserName();
        currentUserEmail = sessionManager.getUserEmail();

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
        loadLocalData();
    }

    private void loadHeaderData() {
        Cursor cursor = dbHelper.getUserByEmail(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            if (imagePath != null && !imagePath.isEmpty()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists() && imgHeaderProfile != null) {
                    imgHeaderProfile.setImageURI(Uri.fromFile(imgFile));
                }
            }
            cursor.close();
        }
    }

    private void loadLocalData() {
        // 1. Load Klub yang sudah diikuti
        loadJoinedClubs();
        
        // 2. Load Rekrutmen yang sedang OPEN
        loadAvailableRecruitments();
    }

    private void loadJoinedClubs() {
        myClubsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        
        Cursor cursor = dbHelper.getUserClubs(currentUserName);
        if (cursor != null && cursor.getCount() > 0) {
            tvMyClubsTitle.setVisibility(View.VISIBLE);
            while (cursor.moveToNext()) {
                String clubName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_CLUB));
                View clubCard = inflater.inflate(R.layout.item_club_card, myClubsContainer, false);
                ((TextView) clubCard.findViewById(R.id.tvClubName)).setText(clubName);
                ((TextView) clubCard.findViewById(R.id.tvClubStatus)).setText("MEMBER");
                clubCard.findViewById(R.id.tvClubDeadline).setVisibility(View.GONE);
                
                Button btnAction = clubCard.findViewById(R.id.btnClubAction);
                btnAction.setText("Dashboard");
                btnAction.setOnClickListener(v -> {
                    Intent intent = new Intent(this, UserClubDashboardActivity.class);
                    intent.putExtra("CLUB_NAME", clubName);
                    startActivity(intent);
                });
                myClubsContainer.addView(clubCard);
            }
            cursor.close();
        } else {
            tvMyClubsTitle.setVisibility(View.GONE);
        }
    }

    private void loadAvailableRecruitments() {
        availableClubsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        Cursor cursor = dbHelper.getRecruitmentConfig();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_STATUS));
                
                // HANYA TAMPILKAN JIKA STATUS ADALAH "OPEN"
                if ("OPEN".equalsIgnoreCase(status)) {
                    String clubName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_CLUB));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_DESC));
                    String deadline = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_DEADLINE));

                    View clubCard = inflater.inflate(R.layout.item_club_card, availableClubsContainer, false);
                    ((TextView) clubCard.findViewById(R.id.tvClubName)).setText(clubName);
                    ((TextView) clubCard.findViewById(R.id.tvClubDescription)).setText(desc);
                    ((TextView) clubCard.findViewById(R.id.tvClubDeadline)).setText("Deadline: " + deadline);
                    ((TextView) clubCard.findViewById(R.id.tvClubStatus)).setText("OPEN");
                    
                    Button btnApply = clubCard.findViewById(R.id.btnClubAction);
                    btnApply.setText("Apply Now");
                    btnApply.setOnClickListener(v -> {
                        // Simulasi pendaftaran
                        dbHelper.addNotification("Application Received", currentUserName + " applied for " + clubName, "ADMIN");
                        Toast.makeText(this, "Application submitted for " + clubName, Toast.LENGTH_SHORT).show();
                    });

                    availableClubsContainer.addView(clubCard);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
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
