package com.example.exora.admin;

import android.content.Intent;
import android.database.Cursor;
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
import com.example.exora.model.EventModel;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private TextView btnViewCalendar;
    private LinearLayout dashEventContainer, allAgendasContainer;
    private TextView tvRecentActivityTitle, tvRecentActivityDesc;
    private ImageView btnAdminNotification;
    private View adminNotifBadge;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);
        btnViewCalendar = findViewById(R.id.btnViewCalendar);
        dashEventContainer = findViewById(R.id.dashEventContainer);
        allAgendasContainer = findViewById(R.id.allAgendasContainer);
        tvRecentActivityTitle = findViewById(R.id.tvRecentActivityTitle);
        tvRecentActivityDesc = findViewById(R.id.tvRecentActivityDesc);
        btnAdminNotification = findViewById(R.id.btnAdminNotification);
        adminNotifBadge = findViewById(R.id.adminNotifBadge);

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboardData();
        checkNotifications();
    }

    private void checkNotifications() {
        Cursor cursor = dbHelper.getUnreadNotifications("ADMIN");
        if (cursor != null && cursor.getCount() > 0) {
            adminNotifBadge.setVisibility(View.VISIBLE);
            if (cursor.moveToLast()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_TITLE));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_MESSAGE));
                tvRecentActivityTitle.setText(title);
                tvRecentActivityDesc.setText(message);
            }
            cursor.close();
        } else {
            adminNotifBadge.setVisibility(View.GONE);
        }
    }

    private void refreshDashboardData() {
        List<EventModel> events = dbHelper.getAllEvents();
        dashEventContainer.removeAllViews();
        allAgendasContainer.removeAllViews();
        
        LayoutInflater inflater = LayoutInflater.from(this);

        for (EventModel event : events) {
            // Highlights
            if (!event.getStatus().equalsIgnoreCase("Closed")) {
                View cardView = inflater.inflate(R.layout.item_admin_dashboard_event, dashEventContainer, false);
                ((TextView) cardView.findViewById(R.id.tvDashTime)).setText(event.getTime());
                ((TextView) cardView.findViewById(R.id.tvDashName)).setText(event.getName());
                ((TextView) cardView.findViewById(R.id.tvDashLocation)).setText(event.getLocation());
                cardView.setOnClickListener(v -> openManageEvent(event.getId()));
                dashEventContainer.addView(cardView);
            }

            // All Agendas
            View eventCard = inflater.inflate(R.layout.item_admin_event_card, allAgendasContainer, false);
            ((TextView) eventCard.findViewById(R.id.tvEventName)).setText(event.getName());
            ((TextView) eventCard.findViewById(R.id.tvTimeRange)).setText(event.getTime());
            ((TextView) eventCard.findViewById(R.id.tvLocation)).setText(event.getLocation());
            ((TextView) eventCard.findViewById(R.id.tvStatus)).setText(event.getStatus());
            eventCard.findViewById(R.id.btnManage).setOnClickListener(v -> openManageEvent(event.getId()));
            allAgendasContainer.addView(eventCard);
        }
    }

    private void openManageEvent(int eventId) {
        Intent intent = new Intent(this, AdminManageEventActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
    }

    private void setupNavigation() {
        btnAgenda.setOnClickListener(v -> startActivity(new Intent(this, AdminAgendaActivity.class)));
        btnClub.setOnClickListener(v -> startActivity(new Intent(this, AdminClubActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, AdminProfileActivity.class)));
        btnAdminNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "ADMIN");
            startActivity(intent);
        });
    }
}