package com.example.exora;

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

import com.example.exora.database.DatabaseHelper;

public class NotificationActivity extends AppCompatActivity {

    private LinearLayout notifContainer;
    private TextView tvNotifCount;
    private Button btnClearAll;
    private DatabaseHelper dbHelper;
    private String targetType = "USER"; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        dbHelper = new DatabaseHelper(this);

        // Determine if this is Admin or User notification view
        if (getIntent().hasExtra("TARGET_TYPE")) {
            targetType = getIntent().getStringExtra("TARGET_TYPE");
        }

        Toolbar toolbar = findViewById(R.id.toolbarNotif);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(targetType.equals("ADMIN") ? "Admin Notifications" : "My Notifications");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        notifContainer = findViewById(R.id.notifContainer);
        tvNotifCount = findViewById(R.id.tvNotifCount);
        btnClearAll = findViewById(R.id.btnClearAll);

        loadNotifications();

        btnClearAll.setOnClickListener(v -> {
            dbHelper.markNotificationsAsRead(targetType);
            loadNotifications();
            Toast.makeText(this, "All caught up!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadNotifications() {
        notifContainer.removeAllViews();
        Cursor cursor = dbHelper.getUnreadNotifications(targetType);
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_TITLE));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_MESSAGE));
                
                View notifView = LayoutInflater.from(this).inflate(R.layout.item_notification, notifContainer, false);
                ((TextView) notifView.findViewById(R.id.tvNotifTitle)).setText(title);
                ((TextView) notifView.findViewById(R.id.tvNotifMessage)).setText(message);
                
                notifContainer.addView(notifView);
                count++;
            } while (cursor.moveToNext());
            cursor.close();
        }

        tvNotifCount.setText(count + " New Notifications");
        if (count == 0) {
            tvNotifCount.setText("No new notifications");
            btnClearAll.setVisibility(View.GONE);
            
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("You're all caught up! Check back later for updates.");
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            tvEmpty.setPadding(0, 100, 0, 0);
            tvEmpty.setTextColor(getResources().getColor(R.color.grayText));
            notifContainer.addView(tvEmpty);
        } else {
            btnClearAll.setVisibility(View.VISIBLE);
        }
    }
}
