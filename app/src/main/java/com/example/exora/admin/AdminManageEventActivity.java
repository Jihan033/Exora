package com.example.exora.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exora.R;
import com.example.exora.auth.ApiService;
import com.example.exora.auth.RetrofitClient;
import com.example.exora.auth.SessionManager;
import com.example.exora.database.DatabaseHelper;
import com.example.exora.model.EventModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminManageEventActivity extends AppCompatActivity {

    private TextInputEditText etName, etDate, etTime, etLocation, etDescription;
    private Button btnSave, btnDelete, btnCloseRegistration;
    private LinearLayout participantsContainer;
    private TextView tvAttendanceTitle, tvNoParticipants;
    private DatabaseHelper dbHelper;
    private ApiService apiService;
    private SessionManager sessionManager;
    private int eventId = -1;
    private String currentStatus = "Upcoming";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_event);

        dbHelper = new DatabaseHelper(this);
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etName = findViewById(R.id.etEventName);
        etDate = findViewById(R.id.etEventDate);
        etTime = findViewById(R.id.etEventTime);
        etLocation = findViewById(R.id.etEventLocation);
        etDescription = findViewById(R.id.etEventDescription);
        btnSave = findViewById(R.id.btnUpdateEvent);
        btnDelete = findViewById(R.id.btnCancelEvent);
        btnCloseRegistration = findViewById(R.id.btnCloseRegistration);
        
        participantsContainer = findViewById(R.id.participantsContainer);
        tvAttendanceTitle = findViewById(R.id.tvAttendanceTitle);
        tvNoParticipants = findViewById(R.id.tvNoParticipants);

        // Setup Pickers
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePickerDialog());
        etTime.setFocusable(false);
        etTime.setOnClickListener(v -> showTimePickerDialog());

        if (getIntent().hasExtra("EVENT_ID")) {
            eventId = getIntent().getIntExtra("EVENT_ID", -1);
            loadEventData(eventId);
            loadParticipants(eventId);
            btnDelete.setText("Delete Event");
        }

        btnSave.setOnClickListener(v -> saveEvent());
        btnDelete.setOnClickListener(v -> {
            if (eventId != -1) {
                dbHelper.deleteEvent(eventId);
                Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                finish();
            }
        });

        btnCloseRegistration.setOnClickListener(v -> closeRegistration());
    }

    private void loadEventData(int id) {
        EventModel event = dbHelper.getEvent(id);
        if (event != null) {
            etName.setText(event.getName());
            etDate.setText(event.getDate());
            etTime.setText(event.getTime());
            etLocation.setText(event.getLocation());
            etDescription.setText(event.getDescription());
            currentStatus = event.getStatus();

            if ("Closed".equalsIgnoreCase(currentStatus)) {
                btnCloseRegistration.setVisibility(View.GONE);
            } else {
                btnCloseRegistration.setVisibility(View.VISIBLE);
            }
        }
    }

    private void closeRegistration() {
        if (eventId != -1) {
            EventModel event = dbHelper.getEvent(eventId);
            if (event != null) {
                dbHelper.updateEvent(new EventModel(eventId, event.getName(), event.getDate(), 
                        event.getTime(), event.getLocation(), event.getDescription(), "Closed"));
                Toast.makeText(this, "Pendaftaran ditutup", Toast.LENGTH_SHORT).show();
                loadEventData(eventId);
            }
        }
    }

    private void loadParticipants(int id) {
        String token = "Bearer " + sessionManager.getToken();
        apiService.getEventParticipants(token, id).enqueue(new Callback<List<Map<String, String>>>() {
            @Override
            public void onResponse(Call<List<Map<String, String>>> call, Response<List<Map<String, String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayParticipants(response.body());
                } else {
                    loadParticipantsFromLocal(id);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, String>>> call, Throwable t) {
                loadParticipantsFromLocal(id);
            }
        });
    }

    private void displayParticipants(List<Map<String, String>> participants) {
        participantsContainer.removeAllViews();
        int count = participants.size();
        
        if (count > 0) {
            tvNoParticipants.setVisibility(View.GONE);
            LayoutInflater inflater = LayoutInflater.from(this);
            for (Map<String, String> p : participants) {
                String name = p.get("name");
                String status = p.get("status");
                
                View itemView = inflater.inflate(R.layout.item_event_participant, participantsContainer, false);
                ((TextView) itemView.findViewById(R.id.tvParticipantName)).setText(name);
                ((TextView) itemView.findViewById(R.id.tvParticipantStatus)).setText(status != null ? status : "Joined");
                
                participantsContainer.addView(itemView);
            }
        } else {
            tvNoParticipants.setVisibility(View.VISIBLE);
        }
        
        tvAttendanceTitle.setText("Attendance Preview (" + count + " Students)");
    }

    private void loadParticipantsFromLocal(int id) {
        participantsContainer.removeAllViews();
        Cursor cursor = dbHelper.getEventParticipants(id);
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            tvNoParticipants.setVisibility(View.GONE);
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATT_USER_NAME));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATT_STATUS));
                
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_event_participant, participantsContainer, false);
                ((TextView) itemView.findViewById(R.id.tvParticipantName)).setText(name);
                ((TextView) itemView.findViewById(R.id.tvParticipantStatus)).setText(status);
                
                participantsContainer.addView(itemView);
                count++;
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            tvNoParticipants.setVisibility(View.VISIBLE);
        }
        
        tvAttendanceTitle.setText("Attendance Preview (" + count + " Students)");
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                    String selectedDate = months[monthOfYear] + " " + String.format(Locale.getDefault(), "%02d", dayOfMonth) + ", " + year1;
                    etDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String amPm = (hourOfDay < 12) ? "AM" : "PM";
                    int displayHour = (hourOfDay > 12) ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute1, amPm);
                    etTime.setText(selectedTime);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void saveEvent() {
        String name = etName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Name and Date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.updateEvent(new EventModel(eventId, name, date, time, location, desc, currentStatus));
        Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
