package com.example.exora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    Button btnStudent, btnTeacher, btnAdmin;
    TextView tvRegister, tvForgot;
    LinearLayout btnGoogle;

    String selectedRole = "Student";
    GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // INPUT
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // BUTTON LOGIN
        btnLogin = findViewById(R.id.btnLogin);

        // TEXT VIEWS
        tvRegister = findViewById(R.id.tvRegister);
        tvForgot = findViewById(R.id.tvForgot);

        // GOOGLE BUTTON
        btnGoogle = findViewById(R.id.btnGoogle);

        // ROLE BUTTON
        btnStudent = findViewById(R.id.btnStudent);
        btnTeacher = findViewById(R.id.btnTeacher);
        btnAdmin = findViewById(R.id.btnAdmin);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Activity Result Launcher for Google Sign In
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    }
                }
        );

        // DEFAULT ACTIVE
        btnStudent.setBackgroundResource(R.drawable.role_selected);

        // STUDENT
        btnStudent.setOnClickListener(v -> {
            selectedRole = "Student";
            updateRoleUI();
        });

        // TEACHER
        btnTeacher.setOnClickListener(v -> {
            selectedRole = "Teacher";
            updateRoleUI();
        });

        // ADMIN
        btnAdmin.setOnClickListener(v -> {
            selectedRole = "Admin";
            updateRoleUI();
        });

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("School email is required");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }

            // CEK LOGIN KHUSUS ADMIN
            if (selectedRole.equals("Admin")) {
                if (email.equals("exoraorg123@gmail.com") && password.equals("admin123")) {
                    loginSuccess();
                } else {
                    Toast.makeText(this, "Invalid Admin credentials", Toast.LENGTH_SHORT).show();
                    etEmail.setError("Wrong email or password for Admin");
                }
            } else {
                // Untuk Student/Teacher saat ini masih bebas (nanti bisa dihubungkan ke Database)
                loginSuccess();
            }
        });

        // GOOGLE BUTTON CLICK
        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // PINDAH KE REGISTER
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // PINDAH KE FORGOT PASSWORD
        tvForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void updateRoleUI() {
        btnStudent.setBackgroundResource(selectedRole.equals("Student") ? R.drawable.role_selected : android.R.color.transparent);
        btnTeacher.setBackgroundResource(selectedRole.equals("Teacher") ? R.drawable.role_selected : android.R.color.transparent);
        btnAdmin.setBackgroundResource(selectedRole.equals("Admin") ? R.drawable.role_selected : android.R.color.transparent);

        btnStudent.setTextColor(getResources().getColor(selectedRole.equals("Student") ? android.R.color.white : android.R.color.black));
        btnTeacher.setTextColor(getResources().getColor(selectedRole.equals("Teacher") ? android.R.color.white : android.R.color.black));
        btnAdmin.setTextColor(getResources().getColor(selectedRole.equals("Admin") ? android.R.color.white : android.R.color.black));
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Welcome " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
            loginSuccess();
        } catch (ApiException e) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google Sign In failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginSuccess() {
        Toast.makeText(
                LoginActivity.this,
                "Login sebagai " + selectedRole,
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}