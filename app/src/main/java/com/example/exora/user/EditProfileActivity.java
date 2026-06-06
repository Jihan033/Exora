package com.example.exora.user;

import android.content.Intent;
import android.database.Cursor;
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
import com.example.exora.database.DatabaseHelper;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etStudentId, etEmail, etBio;
    private Button btnSave;
    private ImageView btnBack, imgProfile;
    private View btnChangeImage;
    private DatabaseHelper dbHelper;
    private String currentUserName = "Alex Chen"; // Mock
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgProfile.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etStudentId = findViewById(R.id.etStudentId);
        etEmail = findViewById(R.id.etEmail);
        etBio = findViewById(R.id.etBio);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        btnChangeImage = findViewById(R.id.btnChangeImage);

        loadUserData();

        btnBack.setOnClickListener(v -> finish());

        btnChangeImage.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUser(currentUserName);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)));
            etStudentId.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_STUDENT_ID)));
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)));
            etBio.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_BIO)));
            
            String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_IMAGE));
            if (imageUriStr != null && !imageUriStr.isEmpty()) {
                selectedImageUri = Uri.parse(imageUriStr);
                imgProfile.setImageURI(selectedImageUri);
            }
            
            cursor.close();
        }
    }

    private void saveProfile() {
        String newName = etName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : null;

        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateUser(currentUserName, newName, studentId, bio, email, imageUriStr);
        if (result > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
