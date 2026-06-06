package com.example.exora.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exora.R;

public class AdminManageMemberActivity extends AppCompatActivity {

    private LinearLayout memberListContainer;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_member);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbarManageMember);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        memberListContainer = findViewById(R.id.memberListContainer);

        loadMembers();
    }

    private void loadMembers() {
        memberListContainer.removeAllViews();
        Cursor cursor = dbHelper.getAllMembers();

        if (cursor != null && cursor.moveToFirst()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_NAME));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_ROLE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEM_TYPE));

                View itemView = inflater.inflate(R.layout.item_admin_member_card, memberListContainer, false);

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

                // Add long click to delete
                itemView.setOnLongClickListener(v -> {
                    dbHelper.deleteMember(id);
                    Toast.makeText(this, "Member removed", Toast.LENGTH_SHORT).show();
                    loadMembers();
                    return true;
                });

                memberListContainer.addView(itemView);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
