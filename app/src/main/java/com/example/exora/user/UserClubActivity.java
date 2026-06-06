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
import com.example.exora.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UserClubActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private LinearLayout myClubsContainer, availableClubsContainer;
    private TextView tvMyClubsTitle;
    private ImageView imgHeaderProfile;
    private DatabaseHelper dbHelper;
    private final String currentUserName = "Alex Chen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_club);

        dbHelper = new DatabaseHelper(this);

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
        loadClubsContent();
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

    private void loadClubsContent() {
        myClubsContainer.removeAllViews();
        availableClubsContainer.removeAllViews();
        
        LayoutInflater inflater = LayoutInflater.from(this);
        
        // 1. Get clubs where user is a member
        List<String> joinedClubNames = new ArrayList<>();
        Cursor joinedCursor = dbHelper.getUserClubs(currentUserName);
        if (joinedCursor != null && joinedCursor.moveToFirst()) {
            tvMyClubsTitle.setVisibility(View.VISIBLE);
            do {
                String clubName = joinedCursor.getString(joinedCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_CLUB));
                joinedClubNames.add(clubName);
                
                View clubCard = inflater.inflate(R.layout.item_club_card, myClubsContainer, false);
                ((TextView) clubCard.findViewById(R.id.tvClubName)).setText(clubName);
                ((TextView) clubCard.findViewById(R.id.tvClubCategory)).setText("MEMBER");
                ((TextView) clubCard.findViewById(R.id.tvClubStatus)).setText("ACTIVE");
                ((TextView) clubCard.findViewById(R.id.tvClubDescription)).setText("You are a registered member of this club.");
                clubCard.findViewById(R.id.tvClubDeadline).setVisibility(View.GONE);
                
                Button btnAction = clubCard.findViewById(R.id.btnClubAction);
                btnAction.setText("View Dashboard");
                btnAction.setOnClickListener(v -> {
                    Intent intent = new Intent(this, UserClubDashboardActivity.class);
                    intent.putExtra("CLUB_NAME", clubName);
                    startActivity(intent);
                });

                // Show Team Members (Minimal view in list)
                View teamSectionView = clubCard.findViewById(R.id.teamSection);
                if (teamSectionView != null) {
                    teamSectionView.setVisibility(View.GONE); // Hide team in the main list to keep it clean
                }

                myClubsContainer.addView(clubCard);
            } while (joinedCursor.moveToNext());
            joinedCursor.close();
        } else {
            tvMyClubsTitle.setVisibility(View.GONE);
        }

        // 2. Get Open Recruitments
        Cursor recCursor = dbHelper.getOpenRecruitments();
        if (recCursor != null && recCursor.moveToFirst()) {
            do {
                String clubName = recCursor.getString(recCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_CLUB));
                String deadline = recCursor.getString(recCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_DEADLINE));
                String desc = recCursor.getString(recCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_DESC));
                
                // Skip if already joined
                if (joinedClubNames.contains(clubName)) continue;

                View clubCard = inflater.inflate(R.layout.item_club_card, availableClubsContainer, false);
                ((TextView) clubCard.findViewById(R.id.tvClubName)).setText(clubName);
                ((TextView) clubCard.findViewById(R.id.tvClubDescription)).setText(desc);
                ((TextView) clubCard.findViewById(R.id.tvClubDeadline)).setText("Deadline: " + deadline);
                
                Button btnApply = clubCard.findViewById(R.id.btnClubAction);
                btnApply.setOnClickListener(v -> {
                    Toast.makeText(this, "Applied to " + clubName + "!", Toast.LENGTH_LONG).show();
                    dbHelper.addNotification("New Applicant", currentUserName + " applied to " + clubName, "ADMIN");
                });

                availableClubsContainer.addView(clubCard);
            } while (recCursor.moveToNext());
            recCursor.close();
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
