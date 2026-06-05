package com.example.exora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    Button btnStudent, btnTeacher, btnAdmin;

    String selectedRole = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // INPUT
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // BUTTON LOGIN
        btnLogin = findViewById(R.id.btnLogin);

        // ROLE BUTTON
        btnStudent = findViewById(R.id.btnStudent);
        btnTeacher = findViewById(R.id.btnTeacher);
        btnAdmin = findViewById(R.id.btnAdmin);

        // DEFAULT ACTIVE
        btnStudent.setBackgroundResource(R.drawable.role_selected);

        // STUDENT
        btnStudent.setOnClickListener(v -> {
            selectedRole = "Student";

            btnStudent.setBackgroundResource(R.drawable.role_selected);
            btnTeacher.setBackgroundResource(android.R.color.transparent);
            btnAdmin.setBackgroundResource(android.R.color.transparent);

            btnStudent.setTextColor(getResources().getColor(android.R.color.white));
            btnTeacher.setTextColor(getResources().getColor(android.R.color.black));
            btnAdmin.setTextColor(getResources().getColor(android.R.color.black));
        });

        // TEACHER
        btnTeacher.setOnClickListener(v -> {
            selectedRole = "Teacher";

            btnTeacher.setBackgroundResource(R.drawable.role_selected);
            btnStudent.setBackgroundResource(android.R.color.transparent);
            btnAdmin.setBackgroundResource(android.R.color.transparent);

            btnTeacher.setTextColor(getResources().getColor(android.R.color.white));
            btnStudent.setTextColor(getResources().getColor(android.R.color.black));
            btnAdmin.setTextColor(getResources().getColor(android.R.color.black));
        });

        // ADMIN
        btnAdmin.setOnClickListener(v -> {
            selectedRole = "Admin";

            btnAdmin.setBackgroundResource(R.drawable.role_selected);
            btnStudent.setBackgroundResource(android.R.color.transparent);
            btnTeacher.setBackgroundResource(android.R.color.transparent);

            btnAdmin.setTextColor(getResources().getColor(android.R.color.white));
            btnStudent.setTextColor(getResources().getColor(android.R.color.black));
            btnTeacher.setTextColor(getResources().getColor(android.R.color.black));
        });

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // VALIDASI EMAIL
            if (email.isEmpty()) {
                etEmail.setError("School email is required");
                etEmail.requestFocus();
                return;
            }

            // VALIDASI PASSWORD
            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }

            Toast.makeText(
                    LoginActivity.this,
                    "Login sebagai " + selectedRole,
                    Toast.LENGTH_SHORT
            ).show();

            // PINDAH KE DASHBOARD
            Intent intent = new Intent(
                    LoginActivity.this,
                    DashboardActivity.class
            );

            startActivity(intent);
            finish();
        });
    }
}