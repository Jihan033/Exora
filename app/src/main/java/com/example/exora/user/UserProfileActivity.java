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

import java.io.File;

public class UserProfileActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile, btnAboutUs;
    private LinearLayout joinedClubsContainer;
    private TextView tvProfileName, tvStudentId, tvBio;
    private ImageView imgUserProfile, imgHeaderProfile;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserEmail = sessionManager.getUserEmail();

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
        btnAboutUs = findViewById(R.id.btnAboutUs);
        joinedClubsContainer = findViewById(R.id.joinedClubsContainer);

        setupNavigation();

        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "USER");
            startActivity(intent);
        });

        findViewById(R.id.btnSignOut).setOnClickListener(v -> {
            sessionManager.logoutUser();
            finish();
        });

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        if (btnAboutUs != null) {
            btnAboutUs.setOnClickListener(v -> {
                Intent intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Selalu ambil email terbaru dari session
        currentUserEmail = sessionManager.getUserEmail();
        loadUserData();
        loadJoinedClubs();
    }

    private void loadUserData() {
        // Cari data berdasarkan EMAIL, bukan Nama
        Cursor cursor = dbHelper.getUserByEmail(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME));
            String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_STUDENT_ID));
            String bio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_BIO));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            
            tvProfileName.setText(name);

            if (tvStudentId != null) {
                tvStudentId.setText(studentId.isEmpty() || studentId.equals("-") ? "Student ID not set" : "Student ID: " + studentId);
            }
            
            if (tvBio != null) {
                tvBio.setText(bio.isEmpty() ? "No bio yet." : bio);
            }
            
            if (imagePath != null && !imagePath.isEmpty()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Uri imageUri = Uri.fromFile(imgFile);
                    if (imgUserProfile != null) imgUserProfile.setImageURI(imageUri);
                    if (imgHeaderProfile != null) imgHeaderProfile.setImageURI(imageUri);
                }
            }
            
            cursor.close();
        }
    }

    private void loadJoinedClubs() {
        joinedClubsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        // Ambil nama user saat ini untuk cari klub
        String currentUserName = sessionManager.getUserName();
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
