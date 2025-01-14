package com.roshtune;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String FIRST_TIME_KEY = "first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if the app is opened for the first time
        boolean isFirstTime = prefs.getBoolean(FIRST_TIME_KEY, true);

        if (isFirstTime) {
            // Perform actions for first-time launch
            // Set a flag to indicate that the app has been opened before
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Replace the fragment_container (FrameLayout) with the LaunchFragment1 fragment
            transaction.replace(R.id.fragment_container, new LaunchFragment1());
            // Commit the transaction
            transaction.commit();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FIRST_TIME_KEY, false);
            editor.apply();
        }
        else {
           startActivity(new Intent(getApplicationContext(), MainActivity2.class));
        }
    }
}