package com.example.exora.user;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.exora.auth.ApiService;
import com.example.exora.auth.EventJoinRequest;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;
import com.example.exora.database.DatabaseHelper;
import com.example.exora.model.EventModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAgendaActivity extends AppCompatActivity {

    private static final String TAG = "UserAgendaActivity";
    private LinearLayout btnDashboard, btnAgenda, btnClub, btnProfile;
    private LinearLayout eventListContainer, joinedEventContainer;
    private TextView tvMySchedule, tvMonth;
    private ImageButton btnPrevMonth, btnNextMonth;
    private ImageView imgHeaderProfile;
    private LinearLayout[] dayLayouts = new LinearLayout[7];
    private TextView[] dayTextViews = new TextView[7];
    
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ApiService apiService;
    private String currentUserEmail;
    private String currentUserName;

    private Calendar calendar;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private String selectedDate;

    private List<EventModel> serverEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agenda);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserEmail = sessionManager.getUserEmail();
        currentUserName = sessionManager.getUserName();
        apiService = RetrofitClient.getApiService();
        
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
                fetchEventsFromServer();
            });

            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        fetchEventsFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUserEmail = sessionManager.getUserEmail();
        currentUserName = sessionManager.getUserName();
        loadHeaderData();
        fetchEventsFromServer();
    }

    private void loadHeaderData() {
        Cursor cursor = dbHelper.getUserByEmail(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            if (imagePath != null && !imagePath.isEmpty()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists() && imgHeaderProfile != null) {
                    imgHeaderProfile.setImageURI(Uri.fromFile(imgFile));
                }
            }
            cursor.close();
        }
    }

    private void fetchEventsFromServer() {
        apiService.getEvents().enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serverEvents = response.body();
                    displayEvents();
                } else {
                    serverEvents = dbHelper.getAllEvents();
                    displayEvents();
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                serverEvents = dbHelper.getAllEvents();
                displayEvents();
            }
        });
    }

    private void displayEvents() {
        if (eventListContainer == null || joinedEventContainer == null) return;
        
        eventListContainer.removeAllViews();
        joinedEventContainer.removeAllViews();
        
        List<EventModel> joinedEvents = dbHelper.getEventsForUser(currentUserName);

        LayoutInflater inflater = LayoutInflater.from(this);
        boolean anyJoinedFound = false;
        boolean anyAvailableFound = false;

        for (final EventModel event : joinedEvents) {
            if (event.getDate().equals(selectedDate)) {
                anyJoinedFound = true;
                View cardView = inflater.inflate(R.layout.item_admin_event_card, joinedEventContainer, false);
                setupEventCard(cardView, event, true);
                joinedEventContainer.addView(cardView);
            }
        }
        
        tvMySchedule.setVisibility(anyJoinedFound ? View.VISIBLE : View.GONE);

        for (final EventModel event : serverEvents) {
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
            btnAction.setOnClickListener(v -> joinEvent(event));
        }
    }

    private void joinEvent(EventModel event) {
        String token = "Bearer " + sessionManager.getUserName();
        apiService.joinEvent(token, new EventJoinRequest(event.getId())).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    dbHelper.joinEvent(event.getId(), currentUserName);
                    Toast.makeText(UserAgendaActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                    fetchEventsFromServer();
                } else {
                    Toast.makeText(UserAgendaActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserAgendaActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigation() {
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnClub = findViewById(R.id.btnClub);
        btnProfile = findViewById(R.id.btnProfile);

        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, UserDashboardActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        btnClub.setOnClickListener(v -> {
            startActivity(new Intent(this, UserClubActivity.class));
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            finish();
        });
    }
}