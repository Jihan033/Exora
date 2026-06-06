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

        // UI References
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
            
            // Update Recent Activity with the latest join notification
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
        
        loadUpcomingHighlights(events);
        loadAllAgendas(events);
        // Default recent activity if no new notifications
        if (adminNotifBadge.getVisibility() == View.GONE) {
            updateActiveRecentActivity(events);
        }
    }

    private void loadUpcomingHighlights(List<EventModel> events) {
        dashEventContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (EventModel event : events) {
            if (!event.getStatus().equalsIgnoreCase("Closed")) {
                View cardView = inflater.inflate(R.layout.item_admin_dashboard_event, dashEventContainer, false);
                ((TextView) cardView.findViewById(R.id.tvDashTime)).setText(event.getTime());
                ((TextView) cardView.findViewById(R.id.tvDashName)).setText(event.getName());
                ((TextView) cardView.findViewById(R.id.tvDashLocation)).setText(event.getLocation());
                
                cardView.setOnClickListener(v -> openManageEvent(event.getId()));
                dashEventContainer.addView(cardView);
            }
        }
    }

    private void loadAllAgendas(List<EventModel> events) {
        allAgendasContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (EventModel event : events) {
            View cardView = inflater.inflate(R.layout.item_admin_event_card, allAgendasContainer, false);
            cardView.findViewById(R.id.btnManage).setOnClickListener(v -> openManageEvent(event.getId()));

            ((TextView) cardView.findViewById(R.id.tvTimeRange)).setText(event.getTime());
            ((TextView) cardView.findViewById(R.id.tvEventName)).setText(event.getName());
            ((TextView) cardView.findViewById(R.id.tvLocation)).setText("📍 " + event.getLocation());
            ((TextView) cardView.findViewById(R.id.tvStatus)).setText(event.getStatus());

            allAgendasContainer.addView(cardView);
        }
    }

    private void updateActiveRecentActivity(List<EventModel> events) {
        EventModel priorityEvent = null;
        for (EventModel e : events) {
            if (e.getStatus().equalsIgnoreCase("Ongoing")) {
                priorityEvent = e;
                break;
            }
        }
        if (priorityEvent == null) {
            for (EventModel e : events) {
                if (e.getStatus().equalsIgnoreCase("Registration Open")) {
                    priorityEvent = e;
                    break;
                }
            }
        }

        if (priorityEvent != null) {
            tvRecentActivityTitle.setText("Active: " + priorityEvent.getName());
            tvRecentActivityDesc.setText(priorityEvent.getStatus() + " | " + priorityEvent.getLocation());
        } else {
            tvRecentActivityTitle.setText("No Active Events");
            tvRecentActivityDesc.setText("Stay tuned for new updates.");
        }
    }

    private void openManageEvent(int eventId) {
        Intent intent = new Intent(this, AdminManageEventActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
    }

    private void setupNavigation() {
        btnDashboard.setOnClickListener(v -> {});

        btnAgenda.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnClub.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminClubActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnViewCalendar.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnAdminNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "ADMIN");
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });
    }
}
