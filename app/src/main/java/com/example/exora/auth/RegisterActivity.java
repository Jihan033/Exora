package com.example.exora.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;
import com.example.exora.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullName, etStudentId, etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvBackToLogin;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etFullName = findViewById(R.id.etFullName);
        etStudentId = findViewById(R.id.etStudentId);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String studentId = etStudentId.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (fullName.isEmpty() || studentId.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            // SIMPAN KE DATABASE ASLI DENGAN STUDENT ID
            long result = dbHelper.registerUser(fullName, email, password, studentId);
            if (result != -1) {
                Toast.makeText(RegisterActivity.this, "Registration Successful! Please Sign In.", Toast.LENGTH_LONG).show();
                finish(); // Kembali ke halaman Login
            } else {
                Toast.makeText(RegisterActivity.this, "Registration Failed. Email might already exist.", Toast.LENGTH_SHORT).show();
            }
        });

        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> finish());
        }
    }
}