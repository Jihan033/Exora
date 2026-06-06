package com.example.exora.admin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.exora.R;

public class ClubActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private CardView btnManageMembers, btnRecruitment;
    private LinearLayout memberListContainer;
    private TextView tvTotalMembersCount;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        dbHelper = new DatabaseHelper(this);

        // UI References
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);
        btnManageMembers = findViewById(R.id.btnManageMembers);
        btnRecruitment = findViewById(R.id.btnRecruitment);
        memberListContainer = findViewById(R.id.clubMemberListContainer);
        tvTotalMembersCount = findViewById(R.id.tvTotalMembersCount);

        setupNavigation();

        btnManageMembers.setOnClickListener(v -> {
            Intent intent = new Intent(ClubActivity.this, ManageMemberActivity.class);
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnRecruitment.setOnClickListener(v -> {
            Intent intent = new Intent(ClubActivity.this, RecruitmentActivity.class);
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMembers();
    }

    private void loadMembers() {
        memberListContainer.removeAllViews();
        Cursor cursor = dbHelper.getAllMembers();
        int count = 0;

        if (cursor != null && cursor.moveToFirst()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            do {
                count++;
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_NAME));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_ROLE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_TYPE));

                View itemView = inflater.inflate(R.layout.item_member_card, memberListContainer, false);

                TextView tvName = itemView.findViewById(R.id.tvMemberName);
                TextView tvRole = itemView.findViewById(R.id.tvMemberRole);
                TextView tvTypeBadge = itemView.findViewById(R.id.tvTypeBadge);

                tvName.setText(name);
                tvRole.setText(role);
                tvTypeBadge.setText(type);

                if ("ADMIN".equals(type)) {
                    tvTypeBadge.setBackgroundResource(R.drawable.role_selected);
                    tvTypeBadge.setTextColor(getResources().getColor(android.R.color.white));
                }

                memberListContainer.addView(itemView);
            } while (cursor.moveToNext());
            cursor.close();
        }
        tvTotalMembersCount.setText(String.valueOf(count));
    }

    private void setupNavigation() {
        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(ClubActivity.this, DashboardActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        btnAgenda.setOnClickListener(v -> {
            startActivity(new Intent(ClubActivity.this, AgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(ClubActivity.this, ProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            finish();
        });
    }
}
