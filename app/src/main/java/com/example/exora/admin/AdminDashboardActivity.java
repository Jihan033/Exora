package com.example.exora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;
import com.example.exora.database.DatabaseHelper;
import com.example.exora.model.EventModel;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private TextView btnViewCalendar;
    private LinearLayout dashEventContainer, allAgendasContainer;
    private TextView tvRecentActivityTitle, tvRecentActivityDesc;
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

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboardData();
    }

    private void refreshDashboardData() {
        List<EventModel> events = dbHelper.getAllEvents();
        
        loadUpcomingHighlights(events);
        loadAllAgendas(events);
        updateActiveRecentActivity(events);
    }

    // Menampilkan highlight agenda di horizontal scroll
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

    // Menampilkan seluruh list agenda secara vertikal
    private void loadAllAgendas(List<EventModel> events) {
        allAgendasContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (EventModel event : events) {
            View cardView = inflater.inflate(R.layout.item_admin_event_card, allAgendasContainer, false);
            
            // Sembunyikan tombol manage agar dashboard tetap bersih, atau biarkan jika ingin akses cepat
            cardView.findViewById(R.id.btnManage).setOnClickListener(v -> openManageEvent(event.getId()));

            ((TextView) cardView.findViewById(R.id.tvTimeRange)).setText(event.getTime());
            ((TextView) cardView.findViewById(R.id.tvEventName)).setText(event.getName());
            ((TextView) cardView.findViewById(R.id.tvLocation)).setText("📍 " + event.getLocation());
            ((TextView) cardView.findViewById(R.id.tvStatus)).setText(event.getStatus());

            allAgendasContainer.addView(cardView);
        }
    }

    // Menyesuaikan Recent Activity dengan agenda yang paling "Aktif"
    private void updateActiveRecentActivity(List<EventModel> events) {
        EventModel priorityEvent = null;

        // Cari yang Ongoing dulu
        for (EventModel e : events) {
            if (e.getStatus().equalsIgnoreCase("Ongoing")) {
                priorityEvent = e;
                break;
            }
        }

        // Jika tidak ada, cari yang Registration Open
        if (priorityEvent == null) {
            for (EventModel e : events) {
                if (e.getStatus().equalsIgnoreCase("Registration Open")) {
                    priorityEvent = e;
                    break;
                }
            }
        }

        // Terakhir, yang Upcoming
        if (priorityEvent == null && !events.isEmpty()) {
            priorityEvent = events.get(events.size() - 1); // Ambil yang paling baru dibuat
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
        btnDashboard.setOnClickListener(v -> Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show());

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
    }
}
