package com.example.exora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.exora.NotificationActivity;
import com.example.exora.R;
import com.example.exora.database.DatabaseHelper;
import com.example.exora.model.EventModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminAgendaActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private LinearLayout eventListContainer;
    private Button btnCreateEntry;
    private ImageView btnAdminNotification;
    private DatabaseHelper dbHelper;

    private TextView tvMonth;
    private ImageButton btnPrevMonth, btnNextMonth;
    private LinearLayout[] dayLayouts = new LinearLayout[7];
    private TextView[] dayTextViews = new TextView[7];
    
    private Calendar calendar;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_agenda);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();
        selectedDate = dateFormat.format(calendar.getTime());

        // UI References
        eventListContainer = findViewById(R.id.eventListContainer);
        btnCreateEntry = findViewById(R.id.btnCreateEntry);
        tvMonth = findViewById(R.id.tvMonth);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        btnAdminNotification = findViewById(R.id.btnAdminNotification);

        initCalendarViews();
        setupNavigation();
        updateCalendarUI();

        btnCreateEntry.setOnClickListener(v -> {
            Intent intent = new Intent(AdminAgendaActivity.this, CreateEventActivity.class);
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateCalendarUI();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateCalendarUI();
        });

        if (btnAdminNotification != null) {
            btnAdminNotification.setOnClickListener(v -> {
                Intent intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("TARGET_TYPE", "ADMIN");
                startActivity(intent);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            });
        }
    }

    private void initCalendarViews() {
        dayLayouts[0] = findViewById(R.id.dayMon);
        dayLayouts[1] = findViewById(R.id.dayTue);
        dayLayouts[2] = findViewById(R.id.dayWed);
        dayLayouts[3] = findViewById(R.id.dayThu);
        dayLayouts[4] = findViewById(R.id.dayFri);
        dayLayouts[5] = findViewById(R.id.daySat);
        dayLayouts[6] = findViewById(R.id.daySun);

        dayTextViews[0] = findViewById(R.id.tvDayMon);
        dayTextViews[1] = findViewById(R.id.tvDayTue);
        dayTextViews[2] = findViewById(R.id.tvDayWed);
        dayTextViews[3] = findViewById(R.id.tvDayThu);
        dayTextViews[4] = findViewById(R.id.tvDayFri);
        dayTextViews[5] = findViewById(R.id.tvDaySat);
        dayTextViews[6] = findViewById(R.id.tvDaySun);
    }

    private void updateCalendarUI() {
        tvMonth.setText(monthFormat.format(calendar.getTime()));
        
        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.setFirstDayOfWeek(Calendar.MONDAY);
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        for (int i = 0; i < 7; i++) {
            final String dateStr = dateFormat.format(tempCal.getTime());
            final int dayNum = tempCal.get(Calendar.DAY_OF_MONTH);
            
            dayTextViews[i].setText(String.format(Locale.getDefault(), "%02d", dayNum));
            
            if (dateStr.equals(selectedDate)) {
                dayLayouts[i].setBackgroundResource(R.drawable.role_selected);
                if (dayLayouts[i].getChildCount() > 0 && dayLayouts[i].getChildAt(0) instanceof TextView) {
                    ((TextView)dayLayouts[i].getChildAt(0)).setTextColor(ContextCompat.getColor(this, android.R.color.white));
                }
                dayTextViews[i].setTextColor(ContextCompat.getColor(this, android.R.color.white));
            } else {
                dayLayouts[i].setBackgroundResource(0);
                if (dayLayouts[i].getChildCount() > 0 && dayLayouts[i].getChildAt(0) instanceof TextView) {
                    ((TextView)dayLayouts[i].getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.grayText));
                }
                dayTextViews[i].setTextColor(ContextCompat.getColor(this, R.color.darkText));
            }

            dayLayouts[i].setOnClickListener(v -> {
                selectedDate = dateStr;
                updateCalendarUI();
                loadEvents();
            });

            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        loadEvents();
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
        boolean found = false;

        for (final EventModel event : events) {
            // Filter by selected date
            if (event.getDate().equals(selectedDate)) {
                found = true;
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
                btnManage.setText("Manage Event");
                btnManage.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminAgendaActivity.this, AdminManageEventActivity.class);
                    intent.putExtra("EVENT_ID", event.getId());
                    intent.putExtra("EVENT_NAME", event.getName());
                    startActivity(intent);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                });

                eventListContainer.addView(cardView);
            }
        }
        
        if (!found) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No activities scheduled for this date.");
            tvEmpty.setPadding(20, 50, 20, 50);
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            eventListContainer.addView(tvEmpty);
        }
    }

    private void setupNavigation() {
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);

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
