package com.roshtune;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class UserProfile extends AppCompatActivity {

    private TextView usernameTextView, mobileTextView;
    private ImageView imageView;
    private Button editButton, updateButton;
    private EditText editUsernameEditText, editMobileEditText;
    private View editProfileLayout;

    private DatabaseReference usersRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        usernameTextView = findViewById(R.id.usernameTextView);
        mobileTextView = findViewById(R.id.mobileTextView);
        imageView = findViewById(R.id.userImageView);
        editButton = findViewById(R.id.editButton);
        updateButton = findViewById(R.id.updateButton);
        editUsernameEditText = findViewById(R.id.editUsernameEditText);
        editMobileEditText = findViewById(R.id.editMobileEditText);
        editProfileLayout = findViewById(R.id.editProfileLayout);

        // Set initial visibility
        editProfileLayout.setVisibility(View.GONE);

        // Get user ID from intent or SharedPreferences
        userId = "your_user_id_here";

        // Initialize Firebase Database reference
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);


        // Retrieve user information from Firebase Database
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // Display username and mobile number
                    usernameTextView.setText(user.getUsername());
                    mobileTextView.setText(user.getMobile());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(UserProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for Edit button
        editButton.setOnClickListener(view -> {
            // Toggle visibility of editProfileLayout
            if (editProfileLayout.getVisibility() == View.VISIBLE) {
                editProfileLayout.setVisibility(View.GONE);
            } else {
                editProfileLayout.setVisibility(View.VISIBLE);
            }
        });

        // Set click listener for Update button
        updateButton.setOnClickListener(view -> {
            // Update user information and database
            String newUsername = editUsernameEditText.getText().toString().trim();
            String newMobile = editMobileEditText.getText().toString().trim();

            // Update the UI
            usernameTextView.setText(newUsername);
            mobileTextView.setText(newMobile);

            // Update database
            usersRef.child("username").setValue(newUsername);
            usersRef.child("mobile").setValue(newMobile);

            // Hide the edit profile layout
            editProfileLayout.setVisibility(View.GONE);
        });
    }
}
