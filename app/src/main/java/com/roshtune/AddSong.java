package com.roshtune;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddSong extends AppCompatActivity {

    private EditText editTextMusicName, editTextArtist, editTextMusicLink, editTextBannerLink;
    private Button buttonAdd;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("songs");

        editTextMusicName = findViewById(R.id.editTextMusicName);
        editTextArtist = findViewById(R.id.editTextArtist);
        editTextMusicLink = findViewById(R.id.editTextMusicLink);
        editTextBannerLink = findViewById(R.id.editTextBannerLink);
        buttonAdd = findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSong();
            }
        });
    }

    private void addSong() {
        String musicName = editTextMusicName.getText().toString().trim();
        String artist = editTextArtist.getText().toString().trim();
        String musicLink = editTextMusicLink.getText().toString().trim();
        String bannerLink = editTextBannerLink.getText().toString().trim();

        if (!musicName.isEmpty() && !artist.isEmpty() && !musicLink.isEmpty() && !bannerLink.isEmpty()) {
            String id = databaseReference.push().getKey();
            Song song = new Song(musicName, artist, musicLink, bannerLink);
            databaseReference.child(id).setValue(song);
            // Clear the EditText fields after adding the song
            editTextMusicName.setText("");
            editTextArtist.setText("");
            editTextMusicLink.setText("");
            editTextBannerLink.setText("");
        } else {
            // Display a message if any field is empty
            // You can customize this according to your requirement
            // For simplicity, I'm just showing a Toast message
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
