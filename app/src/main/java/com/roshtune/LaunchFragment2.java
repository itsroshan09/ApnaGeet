package com.roshtune;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LaunchFragment2 extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText usernameEditText, mobileEditText;
    private Uri selectedImageUri;
    private SQLiteDatabase database;

    public LaunchFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_launch2, container, false);

        // Initialize SQLite database
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        database = dbHelper.getWritableDatabase();

        usernameEditText = v.findViewById(R.id.username);
        mobileEditText = v.findViewById(R.id.usermobile);

        Button selectPhotoButton = v.findViewById(R.id.selectphotobtn);
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button submitButton = v.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();
            }
        });

        return v;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            selectedImageUri = data.getData();
        }
    }

    private void saveUserDetails() {
        String username = usernameEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();

        if (username.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter username and mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(getActivity(), "Please select a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the image to internal storage
        saveImageToInternalStorage(selectedImageUri);

        // Insert user details into SQLite database
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_USERNAME, username);
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_MOBILE, mobile);

        long newRowId = database.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(getActivity(), "User details saved successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity2.class));
        } else {
            Toast.makeText(getActivity(), "Failed to save user details", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToInternalStorage(Uri imageUri) {
        // Same as before
    }
}
