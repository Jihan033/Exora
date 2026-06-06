package com.example.exora.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exora.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class AdminManageEventActivity extends AppCompatActivity {

    private TextInputEditText etName, etDate, etTime, etLocation, etDescription;
    private Button btnSave, btnDelete;
    private DatabaseHelper dbHelper;
    private int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_event);

        dbHelper = new DatabaseHelper(this);

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

        // Setup Pickers (Sama seperti di CreateEventActivity)
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePickerDialog());
        etTime.setFocusable(false);
        etTime.setOnClickListener(v -> showTimePickerDialog());

        if (getIntent().hasExtra("EVENT_ID")) {
            eventId = getIntent().getIntExtra("EVENT_ID", -1);
            loadEventData(eventId);
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
    }

    private void loadEventData(int id) {
        EventModel event = dbHelper.getEvent(id);
        if (event != null) {
            etName.setText(event.getName());
            etDate.setText(event.getDate());
            etTime.setText(event.getTime());
            etLocation.setText(event.getLocation());
            etDescription.setText(event.getDescription());
        }
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

        dbHelper.updateEvent(new EventModel(eventId, name, date, time, location, desc, "Upcoming"));
        Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
