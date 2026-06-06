package com.example.exora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;

import java.util.List;

public class AdminAgendaActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private LinearLayout eventListContainer;
    private Button btnCreateEntry;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_agenda);

        dbHelper = new DatabaseHelper(this);

        // UI References
        eventListContainer = findViewById(R.id.eventListContainer);
        btnCreateEntry = findViewById(R.id.btnCreateEntry);

        // Bottom Navigation
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);

        setupNavigation();

        btnCreateEntry.setOnClickListener(v -> {
            // Berpindah ke CreateEventActivity untuk membuat entri baru
            Intent intent = new Intent(AdminAgendaActivity.this, CreateEventActivity.class);
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        if (eventListContainer == null) return;
        
        eventListContainer.removeAllViews();
        List<EventModel> events = dbHelper.getAllEvents();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (final EventModel event : events) {
            View cardView = inflater.inflate(R.layout.item_admin_event_card, eventListContainer, false);

            TextView tvTime = cardView.findViewById(R.id.tvTimeRange);
            TextView tvName = cardView.findViewById(R.id.tvEventName);
            TextView tvLocation = cardView.findViewById(R.id.tvLocation);
            TextView tvStatus = cardView.findViewById(R.id.tvStatus);
            Button btnManage = cardView.findViewById(R.id.btnManage);

            tvTime.setText(event.getTime());
            tvName.setText(event.getName());
            tvLocation.setText("📍 " + event.getLocation());
            tvStatus.setText(event.getStatus());

            btnManage.setOnClickListener(v -> {
                Intent intent = new Intent(AdminAgendaActivity.this, AdminManageEventActivity.class);
                intent.putExtra("EVENT_ID", event.getId());
                startActivity(intent);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            });

            eventListContainer.addView(cardView);
        }
    }

    private void setupNavigation() {
        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(AdminAgendaActivity.this, AdminDashboardActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        btnClub.setOnClickListener(v -> {
            startActivity(new Intent(AdminAgendaActivity.this, AdminClubActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(AdminAgendaActivity.this, AdminProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            finish();
        });
    }
}
