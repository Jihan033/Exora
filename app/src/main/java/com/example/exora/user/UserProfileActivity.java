package com.example.exora.user;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.NotificationActivity;
import com.example.exora.R;
import com.example.exora.database.DatabaseHelper;

public class UserProfileActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private LinearLayout joinedClubsContainer;
    private TextView tvProfileName, tvStudentId, tvBio;
    private ImageView imgUserProfile, imgHeaderProfile;
    private DatabaseHelper dbHelper;
    private String currentUserName = "Alex Chen"; // Should ideally come from Session/Prefs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DatabaseHelper(this);

        // UI References
        tvProfileName = findViewById(R.id.tvProfileName);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvBio = findViewById(R.id.tvBio);
        imgUserProfile = findViewById(R.id.imgUserProfile);
        imgHeaderProfile = findViewById(R.id.imgHeaderProfile);
        
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);
        joinedClubsContainer = findViewById(R.id.joinedClubsContainer);

        setupNavigation();

        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "USER");
            startActivity(intent);
        });

        findViewById(R.id.btnSignOut).setOnClickListener(v -> {
            Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
            finishAffinity();
        });

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        loadJoinedClubs();
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUser(currentUserName);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME));
            String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_STUDENT_ID));
            String bio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_BIO));
            String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            
            tvProfileName.setText(name);
            currentUserName = name;

            if (tvStudentId != null) tvStudentId.setText("Student ID: " + studentId);
            if (tvBio != null) tvBio.setText(bio);
            
            if (imageUriStr != null && !imageUriStr.isEmpty()) {
                Uri imageUri = Uri.parse(imageUriStr);
                if (imgUserProfile != null) imgUserProfile.setImageURI(imageUri);
                if (imgHeaderProfile != null) imgHeaderProfile.setImageURI(imageUri);
            }
            
            cursor.close();
        }
    }

    private void loadJoinedClubs() {
        joinedClubsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        Cursor cursor = dbHelper.getUserClubs(currentUserName);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String clubName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_CLUB));
                
                View card = inflater.inflate(R.layout.item_club_card, joinedClubsContainer, false);
                ((TextView) card.findViewById(R.id.tvClubName)).setText(clubName);
                ((TextView) card.findViewById(R.id.tvClubStatus)).setText("ACTIVE MEMBER");
                card.findViewById(R.id.tvClubDescription).setVisibility(View.GONE);
                card.findViewById(R.id.tvClubDeadline).setVisibility(View.GONE);
                card.findViewById(R.id.btnClubAction).setVisibility(View.GONE);

                joinedClubsContainer.addView(card);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            TextView emptyTv = new TextView(this);
            emptyTv.setText("You haven't joined any organizations yet.");
            emptyTv.setPadding(0, 20, 0, 0);
            joinedClubsContainer.addView(emptyTv);
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

        btnClub.setOnClickListener(v -> {
            startActivity(new Intent(this, UserClubActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });
    }
}
