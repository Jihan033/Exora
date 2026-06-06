package com.example.exora.admin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exora.R;
import com.example.exora.database.DatabaseHelper;

public class AdminRecruitmentActivity extends AppCompatActivity {

    private TextView tvStatusBadge, tvDeadline, tvApplicantCount, tvShortlistedCount;
    private LinearLayout applicantListContainer;
    private Button btnManageRecruitment;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_recruitment);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbarRecruitment);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // UI References
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvDeadline = findViewById(R.id.tvDeadline);
        tvApplicantCount = findViewById(R.id.tvApplicantCount);
        tvShortlistedCount = findViewById(R.id.tvShortlistedCount);
        applicantListContainer = findViewById(R.id.applicantListContainer);
        btnManageRecruitment = findViewById(R.id.btnManageRecruitment);

        btnManageRecruitment.setOnClickListener(v -> {
            Intent intent = new Intent(AdminRecruitmentActivity.this, AdminManageRecruitmentActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecruitmentData();
        loadPendingApplicants();
    }

    private void loadRecruitmentData() {
        Cursor cursor = dbHelper.getRecruitmentConfig();
        if (cursor != null && cursor.moveToFirst()) {
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_STATUS));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_DEADLINE));

            tvStatusBadge.setText(status);
            if ("OPEN".equals(status)) {
                tvStatusBadge.setBackgroundResource(R.drawable.role_selected); 
                tvStatusBadge.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                tvStatusBadge.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                tvStatusBadge.setTextColor(getResources().getColor(android.R.color.white));
            }

            tvDeadline.setText("Deadline: " + deadline);
            cursor.close();
        }

        tvApplicantCount.setText(String.valueOf(dbHelper.getApplicantCount()));
        tvShortlistedCount.setText(String.valueOf(dbHelper.getShortlistedCount()));
    }

    private void loadPendingApplicants() {
        applicantListContainer.removeAllViews();
        Cursor cursor = dbHelper.getApplicantsByStatus("PENDING");

        if (cursor != null && cursor.moveToFirst()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_APP_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_APP_NAME));
                String dept = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_APP_DEPT));

                View itemView = inflater.inflate(R.layout.item_admin_applicant_card, applicantListContainer, false);

                TextView tvName = itemView.findViewById(R.id.tvApplicantName);
                TextView tvDept = itemView.findViewById(R.id.tvApplicantDept);
                Button btnAccept = itemView.findViewById(R.id.btnAccept);
                Button btnReject = itemView.findViewById(R.id.btnReject);

                tvName.setText(name);
                tvDept.setText(dept);

                btnAccept.setOnClickListener(v -> {
                    // Update applicant status
                    dbHelper.updateApplicantStatus(id, "ACCEPTED");
                    
                    // Automatically add to members table
                    dbHelper.addMember(name, dept, "MEMBER");
                    
                    Toast.makeText(this, name + " Accepted and added to members", Toast.LENGTH_SHORT).show();
                    loadRecruitmentData();
                    loadPendingApplicants();
                });

                btnReject.setOnClickListener(v -> {
                    dbHelper.updateApplicantStatus(id, "REJECTED");
                    Toast.makeText(this, name + " Rejected", Toast.LENGTH_SHORT).show();
                    loadRecruitmentData();
                    loadPendingApplicants();
                });

                applicantListContainer.addView(itemView);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
