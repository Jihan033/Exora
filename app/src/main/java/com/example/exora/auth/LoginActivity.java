package com.example.exora.auth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.exora.R;
import com.example.exora.admin.AdminDashboardActivity;
import com.example.exora.database.DatabaseHelper;
import com.example.exora.user.UserDashboardActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    EditText etEmail, etPassword;
    Button btnLogin;
    Button btnStudent, btnAdmin;
    TextView tvRegisterLink, tvOrContinue, tvForgot;
    LinearLayout btnGoogle, llSocialLogin;

    String selectedRole = "Student";
    SessionManager sessionManager;
    DatabaseHelper dbHelper;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard(sessionManager.getUserRole());
            return;
        }

        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        llSocialLogin = findViewById(R.id.llSocialLogin);
        tvOrContinue = findViewById(R.id.tvOrContinue);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvForgot = findViewById(R.id.tvForgot);
        btnStudent = findViewById(R.id.btnStudent);
        btnAdmin = findViewById(R.id.btnAdmin);

        updateRoleUI();

        btnStudent.setOnClickListener(v -> {
            selectedRole = "Student";
            updateRoleUI();
        });

        btnAdmin.setOnClickListener(v -> {
            selectedRole = "Admin";
            updateRoleUI();
        });

        btnLogin.setOnClickListener(v -> handleLogin());
        
        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> signInWithGoogle());
        }
        
        if (tvRegisterLink != null) {
            tvRegisterLink.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            });
        }

        if (tvForgot != null) {
            tvForgot.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            });
        }
    }

    private void updateRoleUI() {
        if (selectedRole.equals("Student")) {
            btnStudent.setBackgroundResource(R.drawable.role_selected);
            btnAdmin.setBackgroundResource(android.R.color.transparent);
            btnStudent.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnAdmin.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            
            if (tvRegisterLink != null) tvRegisterLink.setVisibility(View.VISIBLE);
            if (tvOrContinue != null) tvOrContinue.setVisibility(View.VISIBLE);
            if (llSocialLogin != null) llSocialLogin.setVisibility(View.VISIBLE);
        } else {
            btnAdmin.setBackgroundResource(R.drawable.role_selected);
            btnStudent.setBackgroundResource(android.R.color.transparent);
            btnAdmin.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnStudent.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            
            if (tvRegisterLink != null) tvRegisterLink.setVisibility(View.GONE);
            if (tvOrContinue != null) tvOrContinue.setVisibility(View.GONE);
            if (llSocialLogin != null) llSocialLogin.setVisibility(View.GONE);
        }
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // LOGIN ADMIN TETAP
        if (selectedRole.equals("Admin")) {
            if (email.equals("exoraorg123@gmail.com") && password.equals("admin123")) {
                sessionManager.createLoginSession("admin-token", "Admin Exora", email, "Admin", "ADM001");
                navigateToDashboard("Admin");
                return;
            } else {
                Toast.makeText(this, "Invalid Admin credentials", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // LOGIN STUDENT DARI DATABASE
        Cursor cursor = dbHelper.checkUser(email, password);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME));
            String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_STUDENT_ID));

            sessionManager.createLoginSession("user-token", name, email, "Student", studentId);
            Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
            cursor.close();
            navigateToDashboard("Student");
        } else {
            if (cursor != null) cursor.close();
            Toast.makeText(this, "Login failed. Account not found. Please register.", Toast.LENGTH_LONG).show();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            sessionManager.createLoginSession("google-token", account.getDisplayName(), account.getEmail(), "Student", "G-USER");
            navigateToDashboard("Student");
        } catch (ApiException e) {
            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if ("Admin".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }
}