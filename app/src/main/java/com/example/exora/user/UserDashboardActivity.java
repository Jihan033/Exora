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

import java.io.File;
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
    private String currentUserEmail;
    private String currentUserName;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserEmail = sessionManager.getUserEmail();
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
        currentUserEmail = sessionManager.getUserEmail();
        currentUserName = sessionManager.getUserName();
        loadUserData();
        fetchDashboardDataFromServer();
        checkNotifications();
    }

    private void loadUserData() {
        tvWelcomeName.setText("Hello, " + currentUserName + "!");
        
        // Cari data berdasarkan EMAIL
        Cursor cursor = dbHelper.getUserByEmail(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            if (imagePath != null && !imagePath.isEmpty()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    imgProfile.setImageURI(Uri.fromFile(imgFile));
                }
            }
            cursor.close();
        }
    }

    private void fetchDashboardDataFromServer() {
        apiService.getEvents().enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateDashboardUI(response.body());
                } else {
                    updateDashboardUI(dbHelper.getAllEvents());
                }
            }
            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                updateDashboardUI(dbHelper.getAllEvents());
            }
        });
    }

    private void updateDashboardUI(List<EventModel> events) {
        dashEventContainer.removeAllViews();
        recommendationContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        int recCount = 0;
        for (EventModel event : events) {
            // Highlights
            if (event.getStatus().equalsIgnoreCase("Registration Open") || event.getStatus().equalsIgnoreCase("Ongoing")) {
                View cardView = inflater.inflate(R.layout.item_admin_dashboard_event, dashEventContainer, false);
                ((TextView) cardView.findViewById(R.id.tvDashTime)).setText(event.getTime());
                ((TextView) cardView.findViewById(R.id.tvDashName)).setText(event.getName());
                ((TextView) cardView.findViewById(R.id.tvDashLocation)).setText(event.getLocation());
                dashEventContainer.addView(cardView);
            }
            // Recommendations
            if (recCount < 3) {
                View recCard = inflater.inflate(R.layout.item_admin_event_card, recommendationContainer, false);
                ((TextView) recCard.findViewById(R.id.tvEventName)).setText(event.getName());
                ((TextView) recCard.findViewById(R.id.tvTimeRange)).setText(event.getTime());
                ((TextView) recCard.findViewById(R.id.tvStatus)).setText(event.getStatus());
                recCard.findViewById(R.id.btnManage).setVisibility(View.GONE);
                recommendationContainer.addView(recCard);
                recCount++;
            }
        }
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
        btnAgendaNav.setOnClickListener(v -> startActivity(new Intent(this, UserAgendaActivity.class)));
        btnClubNav.setOnClickListener(v -> startActivity(new Intent(this, UserClubActivity.class)));
        btnProfileNav.setOnClickListener(v -> startActivity(new Intent(this, UserProfileActivity.class)));
        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "USER");
            startActivity(intent);
        });
    }
}