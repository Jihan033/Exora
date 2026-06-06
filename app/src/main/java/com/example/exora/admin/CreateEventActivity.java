package com.example.exora.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exora.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private TextInputEditText etName, etDate, etTime, etLocation, etDescription;
    private Spinner spStatus;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_event);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbarCreate);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etName = findViewById(R.id.etNewName);
        etDate = findViewById(R.id.etNewDate);
        etTime = findViewById(R.id.etNewTime);
        etLocation = findViewById(R.id.etNewLocation);
        etDescription = findViewById(R.id.etNewDescription);
        spStatus = findViewById(R.id.spStatus);
        btnSubmit = findViewById(R.id.btnSubmitCreate);

        // Set click listeners for Date and Time pickers
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePickerDialog());

        etTime.setFocusable(false);
        etTime.setOnClickListener(v -> showTimePickerDialog());

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String status = spStatus.getSelectedItem().toString();

            if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in Name, Date, and Time", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbHelper.addEvent(new EventModel(name, date, time, location, desc, status));
            if (id > 0) {
                Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Format: MMM dd, yyyy (e.g., Sep 12, 2024)
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
}
