package com.example.exora.admin;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exora.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class ManageRecruitmentActivity extends AppCompatActivity {

    private RadioGroup rgStatus;
    private RadioButton rbOpen, rbClosed;
    private TextInputEditText etDeadline;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recruitment);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbarManageRec);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rgStatus = findViewById(R.id.rgStatus);
        rbOpen = findViewById(R.id.rbOpen);
        rbClosed = findViewById(R.id.rbClosed);
        etDeadline = findViewById(R.id.etRecDeadline);
        btnSave = findViewById(R.id.btnSaveRecSettings);

        loadCurrentConfig();

        etDeadline.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void loadCurrentConfig() {
        Cursor cursor = dbHelper.getRecruitmentConfig();
        if (cursor != null && cursor.moveToFirst()) {
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_STATUS));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RC_DEADLINE));

            if ("OPEN".equals(status)) {
                rbOpen.setChecked(true);
            } else {
                rbClosed.setChecked(true);
            }
            etDeadline.setText(deadline);
            cursor.close();
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                    String selectedDate = months[monthOfYear] + " " + String.format(Locale.getDefault(), "%02d", dayOfMonth) + ", " + year1;
                    etDeadline.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveSettings() {
        String status = rbOpen.isChecked() ? "OPEN" : "CLOSED";
        String deadline = etDeadline.getText().toString().trim();

        if (deadline.isEmpty()) {
            Toast.makeText(this, "Please select a deadline", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.updateRecruitmentStatus(status, deadline);
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
