package com.example.exora.user;

import android.content.Intent;
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
import com.example.exora.auth.ApiService;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;
import com.example.exora.model.EventModel;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDashboardActivity extends AppCompatActivity {

    private LinearLayout dashEventContainer, recommendationContainer, btnDashboard, btnAgenda, btnClub, btnProfile;
    private TextView tvUserName;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();

        tvUserName = findViewById(R.id.tvWelcomeName);
        dashEventContainer = findViewById(R.id.dashEventContainer);
        recommendationContainer = findViewById(R.id.recommendationContainer);
        btnDashboard = findViewById(R.id.btnDashboard);
        btnAgenda = findViewById(R.id.btnAgendaNav);
        btnClub = findViewById(R.id.btnClubNav);
        btnProfile = findViewById(R.id.btnProfileNav);

        if (tvUserName != null) {
            tvUserName.setText("Hello, " + sessionManager.getUserName() + "!");
        }

        setupNavigation();
        fetchEvents();

        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("TARGET_TYPE", "USER");
            startActivity(intent);
        });
        
        findViewById(R.id.btnViewAllEvents).setOnClickListener(v -> {
            startActivity(new Intent(this, UserAgendaActivity.class));
        });
    }

    private void fetchEvents() {
        apiService.getEvents().enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayEvents(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                Toast.makeText(UserDashboardActivity.this, "Gagal memuat agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEvents(List<EventModel> events) {
        if (dashEventContainer == null || recommendationContainer == null) return;
        
        dashEventContainer.removeAllViews();
        recommendationContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        // Hot Events (limit to 5 or based on status)
        for (int i = 0; i < Math.min(events.size(), 5); i++) {
            EventModel event = events.get(i);
            View card = inflater.inflate(R.layout.item_admin_dashboard_event, dashEventContainer, false);
            ((TextView) card.findViewById(R.id.tvDashTime)).setText(event.getTime());
            ((TextView) card.findViewById(R.id.tvDashName)).setText(event.getName());
            ((TextView) card.findViewById(R.id.tvDashLocation)).setText(event.getLocation());
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, UserAgendaActivity.class);
                intent.putExtra("TARGET_DATE", event.getDate());
                startActivity(intent);
            });
            dashEventContainer.addView(card);
        }

        // Recommended (Shuffle or filter)
        List<EventModel> recommended = new java.util.ArrayList<>(events);
        Collections.shuffle(recommended);
        for (int i = 0; i < Math.min(recommended.size(), 3); i++) {
            EventModel event = recommended.get(i);
            View card = inflater.inflate(R.layout.item_admin_event_card, recommendationContainer, false);
            ((TextView) card.findViewById(R.id.tvEventName)).setText(event.getName());
            ((TextView) card.findViewById(R.id.tvTimeRange)).setText(event.getTime());
            ((TextView) card.findViewById(R.id.tvLocation)).setText(event.getLocation());
            ((TextView) card.findViewById(R.id.tvStatus)).setText(event.getStatus());
            
            // Hide Manage button for users
            card.findViewById(R.id.btnManage).setVisibility(View.GONE);
            
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, UserAgendaActivity.class);
                intent.putExtra("TARGET_DATE", event.getDate());
                startActivity(intent);
            });
            recommendationContainer.addView(card);
        }
    }

    private void setupNavigation() {
        if (btnAgenda != null) {
            btnAgenda.setOnClickListener(v -> {
                startActivity(new Intent(this, UserAgendaActivity.class));
            });
        }
        if (btnClub != null) {
            btnClub.setOnClickListener(v -> {
                startActivity(new Intent(this, UserClubActivity.class));
            });
        }
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, UserProfileActivity.class));
            });
        }
        
        findViewById(R.id.btnSchedules).setOnClickListener(v -> {
             startActivity(new Intent(this, UserAgendaActivity.class));
        });
        
        findViewById(R.id.btnJoinClub).setOnClickListener(v -> {
             startActivity(new Intent(this, UserClubActivity.class));
        });
    }
}
