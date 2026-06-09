package com.example.exora.user;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.exora.NotificationActivity;
import com.example.exora.R;
import com.example.exora.auth.ApiService;
import com.example.exora.auth.EventJoinRequest;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;
import com.example.exora.database.DatabaseHelper;
import com.example.exora.model.EventModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDashboardActivity extends AppCompatActivity {

    private static final String TAG = "UserDashboardActivity";
    private LinearLayout btnDashboard, btnAgendaNav, btnClubNav, btnProfileNav;
    private CardView btnJoinClub, btnSchedules;
    private ImageView btnNotification, imgProfile;
    private View notifBadge;
    private TextView btnViewAllEvents, tvWelcomeName;
    private LinearLayout dashEventContainer, recommendationContainer;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String currentUserName;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserName = sessionManager.getUserName();
        apiService = RetrofitClient.getApiService();

        // UI References
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgendaNav = findViewById(R.id.btnAgendaNav);
        btnClubNav = findViewById(R.id.btnClubNav);
        btnProfileNav = findViewById(R.id.btnProfileNav);
        
        btnJoinClub = findViewById(R.id.btnJoinClub);
        btnSchedules = findViewById(R.id.btnSchedules);
        btnNotification = findViewById(R.id.btnNotification);
        imgProfile = findViewById(R.id.imgProfile);
        notifBadge = findViewById(R.id.notifBadge);
        
        btnViewAllEvents = findViewById(R.id.btnViewAllEvents);
        dashEventContainer = findViewById(R.id.dashEventContainer);
        recommendationContainer = findViewById(R.id.recommendationContainer);

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        fetchDashboardDataFromServer();
        checkNotifications();
    }

    private void loadUserData() {
        if (currentUserName == null) return;
        
        tvWelcomeName.setText("Hello, " + currentUserName + "!");
        
        // Optional: still load local bio/image if available
        Cursor cursor = dbHelper.getUser(currentUserName);
        if (cursor != null && cursor.moveToFirst()) {
            String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            if (imageUriStr != null && !imageUriStr.isEmpty()) {
                imgProfile.setImageURI(Uri.parse(imageUriStr));
            }
            cursor.close();
        }
    }

    private void fetchDashboardDataFromServer() {
        apiService.getEvents().enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventModel> events = response.body();
                    updateDashboardUI(events);
                } else {
                    Log.e(TAG, "Failed to fetch events: " + response.message());
                    // Fallback to local data if server fails
                    updateDashboardUI(dbHelper.getAllEvents());
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                Log.e(TAG, "Network error fetching events", t);
                updateDashboardUI(dbHelper.getAllEvents());
            }
        });
    }

    private void updateDashboardUI(List<EventModel> events) {
        loadHotEvents(events);
        loadRecommendations(events);
    }

    private void loadHotEvents(List<EventModel> events) {
        dashEventContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (EventModel event : events) {
            if (event.getStatus().equalsIgnoreCase("Registration Open") || event.getStatus().equalsIgnoreCase("Ongoing")) {
                View cardView = inflater.inflate(R.layout.item_admin_dashboard_event, dashEventContainer, false);
                ((TextView) cardView.findViewById(R.id.tvDashTime)).setText(event.getTime());
                ((TextView) cardView.findViewById(R.id.tvDashName)).setText(event.getName());
                ((TextView) cardView.findViewById(R.id.tvDashLocation)).setText(event.getLocation());
                
                cardView.setOnClickListener(v -> {
                    Toast.makeText(this, "Viewing " + event.getName(), Toast.LENGTH_SHORT).show();
                });
                dashEventContainer.addView(cardView);
            }
        }
    }

    private void loadRecommendations(List<EventModel> events) {
        recommendationContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        int count = 0;
        for (EventModel event : events) {
            if (count >= 3) break;

            View cardView = inflater.inflate(R.layout.item_admin_event_card, recommendationContainer, false);
            View btnAction = cardView.findViewById(R.id.btnManage);
            if (btnAction instanceof TextView) {
                ((TextView) btnAction).setText("Join Event");
            }
            
            btnAction.setOnClickListener(v -> joinEventOnServer(event));

            ((TextView) cardView.findViewById(R.id.tvTimeRange)).setText(event.getTime());
            ((TextView) cardView.findViewById(R.id.tvEventName)).setText(event.getName());
            ((TextView) cardView.findViewById(R.id.tvLocation)).setText("📍 " + event.getLocation());
            ((TextView) cardView.findViewById(R.id.tvStatus)).setText(event.getStatus());

            recommendationContainer.addView(cardView);
            count++;
        }
    }

    private void joinEventOnServer(EventModel event) {
        // SharedPreferences based token
        String token = "Bearer " + sessionManager.getUserName(); // Simulation token
        
        apiService.joinEvent(token, new EventJoinRequest(event.getId())).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Sync local DB as well for persistence/offline
                    dbHelper.joinEvent(event.getId(), currentUserName);
                    Toast.makeText(UserDashboardActivity.this, "Successfully joined " + event.getName(), Toast.LENGTH_SHORT).show();
                    fetchDashboardDataFromServer();
                } else {
                    Toast.makeText(UserDashboardActivity.this, "Failed to join event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkNotifications() {
        Cursor cursor = dbHelper.getUnreadNotifications("USER");
        if (cursor != null && cursor.getCount() > 0) {
            notifBadge.setVisibility(View.VISIBLE);
            cursor.close();
        } else {
            notifBadge.setVisibility(View.GONE);
        }
    }

    private void setupNavigation() {
        btnDashboard.setOnClickListener(v -> {}); // Stay here

        btnAgendaNav.setOnClickListener(v -> {
            startActivity(new Intent(this, UserAgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnClubNav.setOnClickListener(v -> {
            startActivity(new Intent(this, UserClubActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnProfileNav.setOnClickListener(v -> {
            startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnJoinClub.setOnClickListener(v -> {
            startActivity(new Intent(this, UserClubActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnSchedules.setOnClickListener(v -> {
            startActivity(new Intent(this, UserAgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "USER");
            startActivity(intent);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        btnViewAllEvents.setOnClickListener(v -> {
            startActivity(new Intent(this, UserAgendaActivity.class));
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });
    }
}
