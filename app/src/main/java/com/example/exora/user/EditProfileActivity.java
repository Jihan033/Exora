package com.example.exora.user;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exora.R;
import com.example.exora.auth.SessionManager;
import com.example.exora.database.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etStudentId, etEmail, etBio;
    private Button btnSave;
    private ImageView btnBack, imgProfile;
    private View btnChangeImage;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String currentUserEmail;
    private String selectedImagePath = null;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    saveImageToInternalStorage(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserEmail = sessionManager.getUserEmail();

        etName = findViewById(R.id.etName);
        etStudentId = findViewById(R.id.etStudentId);
        etEmail = findViewById(R.id.etEmail);
        etBio = findViewById(R.id.etBio);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        btnChangeImage = findViewById(R.id.btnChangeImage);

        // Lock Email
        etEmail.setEnabled(false);
        etEmail.setFocusable(false);
        etEmail.setAlpha(0.6f);

        loadUserData();

        btnBack.setOnClickListener(v -> finish());
        btnChangeImage.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserByEmail(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)));
            etStudentId.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_STUDENT_ID)));
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)));
            etBio.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_BIO)));
            
            selectedImagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                File imgFile = new File(selectedImagePath);
                if (imgFile.exists()) {
                    imgProfile.setImageURI(Uri.fromFile(imgFile));
                }
            }
            cursor.close();
        }
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            String filename = "profile_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), filename);
            
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            
            selectedImagePath = file.getAbsolutePath();
            imgProfile.setImageBitmap(bitmap);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        String newName = etName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fixed: Use updateUserByEmail instead of updateUser
        int result = dbHelper.updateUserByEmail(currentUserEmail, newName, studentId, bio, selectedImagePath);
        if (result > 0) {
            // Update session
            sessionManager.createLoginSession(
                    sessionManager.getToken(),
                    newName,
                    currentUserEmail,
                    sessionManager.getUserRole(),
                    studentId
            );
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
