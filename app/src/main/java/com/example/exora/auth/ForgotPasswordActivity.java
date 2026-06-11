package com.example.exora.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;

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

            // Simulated reset link sent
            Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to " + email, Toast.LENGTH_LONG).show();
            finish();
        });

        if (tvBackToLoginForgot != null) {
            tvBackToLoginForgot.setOnClickListener(v -> {
                finish();
            });
        }
    }
}