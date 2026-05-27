package com.example.exora;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmailForgot;
    Button btnResetPassword;
    TextView tvBackToLoginForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmailForgot = findViewById(R.id.etEmailForgot);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLoginForgot = findViewById(R.id.tvBackToLoginForgot);

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmailForgot.getText().toString().trim();

            if (email.isEmpty()) {
                etEmailForgot.setError("Email is required");
                etEmailForgot.requestFocus();
                return;
            }

            // Simulasi pengiriman email reset
            Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to " + email, Toast.LENGTH_LONG).show();
            finish();
        });

        tvBackToLoginForgot.setOnClickListener(v -> {
            finish();
        });
    }
}