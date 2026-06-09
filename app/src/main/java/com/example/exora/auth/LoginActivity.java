package com.example.exora.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;
import com.example.exora.admin.AdminDashboardActivity;
import com.example.exora.user.UserDashboardActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    EditText etEmail, etPassword;
    Button btnLogin;
    Button btnStudent, btnAdmin;
    TextView tvRegisterLink, tvOrContinue;
    LinearLayout btnGoogle, llSocialLogin;

    String selectedRole = "Student";
    SessionManager sessionManager;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        
        // Check session
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard(sessionManager.getUserRole());
            return;
        }

        setContentView(R.layout.activity_login);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // INPUT
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // BUTTON LOGIN
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        llSocialLogin = findViewById(R.id.llSocialLogin);
        tvOrContinue = findViewById(R.id.tvOrContinue);
        
        // REGISTER LINK
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // ROLE BUTTON
        btnStudent = findViewById(R.id.btnStudent);
        btnAdmin = findViewById(R.id.btnAdmin);

        // DEFAULT ACTIVE
        btnStudent.setBackgroundResource(R.drawable.role_selected);

        // STUDENT
        btnStudent.setOnClickListener(v -> {
            selectedRole = "Student";
            updateRoleUI();
        });

        // ADMIN
        btnAdmin.setOnClickListener(v -> {
            selectedRole = "Admin";
            updateRoleUI();
        });

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> handleLogin());
        
        // GOOGLE BUTTON
        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> signInWithGoogle());
        }
        
        // REGISTER LINK
        if (tvRegisterLink != null) {
            tvRegisterLink.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            });
        }
    }

    private void updateRoleUI() {
        if (selectedRole.equals("Student")) {
            btnStudent.setBackgroundResource(R.drawable.role_selected);
            btnAdmin.setBackgroundResource(android.R.color.transparent);
            btnStudent.setTextColor(getResources().getColor(android.R.color.white));
            btnAdmin.setTextColor(getResources().getColor(android.R.color.black));
            
            // Show registration and social login for Student
            if (tvRegisterLink != null) tvRegisterLink.setVisibility(View.VISIBLE);
            if (tvOrContinue != null) tvOrContinue.setVisibility(View.VISIBLE);
            if (llSocialLogin != null) llSocialLogin.setVisibility(View.VISIBLE);
        } else {
            btnAdmin.setBackgroundResource(R.drawable.role_selected);
            btnStudent.setBackgroundResource(android.R.color.transparent);
            btnAdmin.setTextColor(getResources().getColor(android.R.color.white));
            btnStudent.setTextColor(getResources().getColor(android.R.color.black));
            
            // Hide registration and social login for Admin
            if (tvRegisterLink != null) tvRegisterLink.setVisibility(View.GONE);
            if (tvOrContinue != null) tvOrContinue.setVisibility(View.GONE);
            if (llSocialLogin != null) llSocialLogin.setVisibility(View.GONE);
        }
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password, selectedRole);
        
        RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    UserResponse user = auth.getUser();
                    
                    sessionManager.createLoginSession(
                            auth.getToken(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            user.getStudentId()
                    );

                    Toast.makeText(LoginActivity.this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
                    navigateToDashboard(user.getRole());
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed: Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

            // Successfully signed in with Google
            // In a real app, you would send the ID Token to your backend.
            // For now, we simulate a successful login as "Student"
            sessionManager.createLoginSession(
                    "google-mock-token",
                    account.getDisplayName(),
                    account.getEmail(),
                    "Student",
                    "G-" + (account.getId() != null ? account.getId().substring(0, 8) : "12345")
            );

            Toast.makeText(this, "Google Sign-In Successful: " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
            navigateToDashboard("Student");

        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
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
