package com.example.exora.user;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class UserAgendaActivity extends AppCompatActivity {

    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private LinearLayout eventListContainer, joinedEventContainer;
    private TextView tvMySchedule, tvMonth;
    private ImageButton btnPrevMonth, btnNextMonth;
    private ImageView imgHeaderProfile;
    private LinearLayout[] dayLayouts = new LinearLayout[7];
    private TextView[] dayTextViews = new TextView[7];
    
    private DatabaseHelper dbHelper;
    private final String currentUserName = "Alex Chen"; // Mock current user

    private Calendar calendar;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agenda);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();
        selectedDate = dateFormat.format(calendar.getTime());

        // UI References
        eventListContainer = findViewById(R.id.eventListContainer);
        joinedEventContainer = findViewById(R.id.joinedEventContainer);
        tvMySchedule = findViewById(R.id.tvMySchedule);
        tvMonth = findViewById(R.id.tvMonth);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        imgHeaderProfile = findViewById(R.id.imgHeaderProfile);

        initCalendarViews();
        setupNavigation();
        updateCalendarUI();

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateCalendarUI();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateCalendarUI();
        });

        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "USER");
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });
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
                ((TextView)dayLayouts[i].getChildAt(0)).setTextColor(ContextCompat.getColor(this, android.R.color.white));
                dayTextViews[i].setTextColor(ContextCompat.getColor(this, android.R.color.white));
            } else {
                dayLayouts[i].setBackgroundResource(0);
                ((TextView)dayLayouts[i].getChildAt(0)).setTextColor(ContextCompat.getColor(this, android.R.color.black));
                dayTextViews[i].setTextColor(ContextCompat.getColor(this, android.R.color.black));
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
        loadHeaderData();
        loadEvents();
    }

    private void loadHeaderData() {
        Cursor cursor = dbHelper.getUser(currentUserName);
        if (cursor != null && cursor.moveToFirst()) {
            String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            if (imageUriStr != null && !imageUriStr.isEmpty()) {
                if (imgHeaderProfile != null) {
                    imgHeaderProfile.setImageURI(Uri.parse(imageUriStr));
                }
            }
            cursor.close();
        }
    }

    private void loadEvents() {
        if (eventListContainer == null || joinedEventContainer == null) return;
        
        eventListContainer.removeAllViews();
        joinedEventContainer.removeAllViews();
        
        List<EventModel> allEvents = dbHelper.getAllEvents();
        List<EventModel> joinedEvents = dbHelper.getEventsForUser(currentUserName);

        LayoutInflater inflater = LayoutInflater.from(this);
        boolean anyJoinedFound = false;
        boolean anyAvailableFound = false;

        // 1. Load Joined Events for selected date
        for (final EventModel event : joinedEvents) {
            if (event.getDate().equals(selectedDate)) {
                anyJoinedFound = true;
                View cardView = inflater.inflate(R.layout.item_admin_event_card, joinedEventContainer, false);
                setupEventCard(cardView, event, true);
                joinedEventContainer.addView(cardView);
            }
        }
        
        tvMySchedule.setVisibility(anyJoinedFound ? View.VISIBLE : View.GONE);

        // 2. Load Available Events (Not joined yet) for selected date
        for (final EventModel event : allEvents) {
            if (!event.getDate().equals(selectedDate)) continue;

            boolean alreadyJoined = false;
            for (EventModel je : joinedEvents) {
                if (je.getId() == event.getId()) {
                    alreadyJoined = true;
                    break;
                }
            }

            if (!alreadyJoined) {
                anyAvailableFound = true;
                View cardView = inflater.inflate(R.layout.item_admin_event_card, eventListContainer, false);
                setupEventCard(cardView, event, false);
                eventListContainer.addView(cardView);
            }
        }

        if (!anyJoinedFound && !anyAvailableFound) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No activities scheduled for this date.");
            tvEmpty.setPadding(20, 50, 20, 50);
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            eventListContainer.addView(tvEmpty);
        }
    }

    private void setupEventCard(View cardView, EventModel event, boolean isJoined) {
        TextView tvTime = cardView.findViewById(R.id.tvTimeRange);
        TextView tvName = cardView.findViewById(R.id.tvEventName);
        TextView tvLocation = cardView.findViewById(R.id.tvLocation);
        TextView tvStatus = cardView.findViewById(R.id.tvStatus);
        Button btnAction = cardView.findViewById(R.id.btnManage);

        tvTime.setText(event.getTime());
        tvName.setText(event.getName());
        tvLocation.setText("📍 " + event.getLocation());
        tvStatus.setText(event.getStatus());
        
        if (isJoined) {
            btnAction.setText("Registered");
            btnAction.setEnabled(false);
            btnAction.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        } else {
            btnAction.setText("Join Event");
            btnAction.setOnClickListener(v -> {
                dbHelper.joinEvent(event.getId(), currentUserName);
                Toast.makeText(this, "Successfully registered for " + event.getName(), Toast.LENGTH_SHORT).show();
                loadEvents(); // Refresh lists
            });
        }
    }

    private void setupNavigation() {
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);

        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(UserAgendaActivity.this, UserDashboardActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        btnClub.setOnClickListener(v -> {
            startActivity(new Intent(UserAgendaActivity.this, UserClubActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(UserAgendaActivity.this, UserProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            finish();
        });
    }
}
