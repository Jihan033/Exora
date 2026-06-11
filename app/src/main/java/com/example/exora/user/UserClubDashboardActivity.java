package com.example.exora.user;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.exora.R;
import com.example.exora.auth.SessionManager;
import com.example.exora.database.DatabaseHelper;

public class UserClubDashboardActivity extends AppCompatActivity {

    private String clubName;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private LinearLayout teamContainer;
    private TextView tvClubName, tvClubDescription, tvMemberStatus, tvMemberCount, tvEventCount;
    private LinearLayout btnClubCalendar, btnClubHandbook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_club_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        clubName = getIntent().getStringExtra("CLUB_NAME");

        if (clubName == null) {
            finish();
            return;
        }

        setupToolbar();
        initViews();
        loadClubData();
        loadTeamMembers();
        setupInternalResources();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(clubName);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        tvClubName = findViewById(R.id.tvClubName);
        tvClubDescription = findViewById(R.id.tvClubDescription);
        tvMemberStatus = findViewById(R.id.tvMemberStatus);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        tvEventCount = findViewById(R.id.tvEventCount);
        teamContainer = findViewById(R.id.teamContainer);
        btnClubCalendar = findViewById(R.id.btnClubCalendar);
        btnClubHandbook = findViewById(R.id.btnClubHandbook);
        
        tvClubName.setText(clubName);
    }

    private void loadClubData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Load description from recruitment config
        Cursor cursor = db.query(DatabaseHelper.TABLE_RECRUITMENT_CONFIG, 
                new String[]{DatabaseHelper.COLUMN_RC_DESC}, 
                DatabaseHelper.COLUMN_RC_CLUB + " = ?", 
                new String[]{clubName}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            tvClubDescription.setText(cursor.getString(0));
            cursor.close();
        }

        // Count members
        Cursor memberCursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_MEMBERS + 
                " WHERE " + DatabaseHelper.COLUMN_MEM_CLUB + " = ?", new String[]{clubName});
        if (memberCursor != null && memberCursor.moveToFirst()) {
            tvMemberCount.setText(String.valueOf(memberCursor.getInt(0)));
            memberCursor.close();
        }

        // Load specific member role for current user (Sync with session)
        String currentUserName = sessionManager.getUserName();
        Cursor roleCursor = db.query(DatabaseHelper.TABLE_MEMBERS,
                new String[]{DatabaseHelper.COLUMN_MEM_ROLE},
                DatabaseHelper.COLUMN_MEM_CLUB + " = ? AND " + DatabaseHelper.COLUMN_MEM_NAME + " = ?",
                new String[]{clubName, currentUserName}, null, null, null);
        
        if (roleCursor != null && roleCursor.moveToFirst()) {
            tvMemberStatus.setText(roleCursor.getString(0));
            roleCursor.close();
        } else {
            tvMemberStatus.setText("Member");
        }

        // Dummy project count
        tvEventCount.setText("5");
    }

    private void loadTeamMembers() {
        teamContainer.removeAllViews();
        Cursor cursor = dbHelper.getMembersByClub(clubName);
        if (cursor != null && cursor.moveToFirst()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            int count = 0;
            do {
                if (count >= 3) break; // Only show top 3 in dashboard
                
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_NAME));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_ROLE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_TYPE));

                View memberItem = inflater.inflate(R.layout.item_admin_member_card, teamContainer, false);
                ((TextView) memberItem.findViewById(R.id.tvMemberName)).setText(name);
                ((TextView) memberItem.findViewById(R.id.tvMemberRole)).setText(role);
                TextView tvBadge = memberItem.findViewById(R.id.tvTypeBadge);
                tvBadge.setText(type);
                
                if ("ADMIN".equals(type)) {
                    tvBadge.setBackgroundResource(R.drawable.role_selected);
                    tvBadge.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                }

                teamContainer.addView(memberItem);
                count++;
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void setupInternalResources() {
        btnClubCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserAgendaActivity.class);
            startActivity(intent);
        });

        btnClubHandbook.setOnClickListener(v -> {
            Toast.makeText(this, "Opening " + clubName + " Member Handbook (PDF)...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnSeeAllMembers).setOnClickListener(v -> {
            Toast.makeText(this, "Showing all members of " + clubName, Toast.LENGTH_SHORT).show();
        });
    }
}
