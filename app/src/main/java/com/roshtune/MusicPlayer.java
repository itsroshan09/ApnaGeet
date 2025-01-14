package com.roshtune;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity {
    private ImageView songImage;
    String currentartist,currentmusicName,currentmusicLink,currentbannerLink;
    String pastartist,pastmusicName,pastmusicLink,pastbannerLink;
    private MediaPlayer mediaPlayer;
    private String musicLink;

    private TextView artist;
    int finalsong;
    String songimage;
    int backcount = 0;
    ImageView songimage2;
    private final List<Song> songDataList = new ArrayList<>();
    private static final String CHANNEL_ID = "MyChannel";
    TextView songname, currenttime, duration2;
    SeekBar sb;
    Timer timer;
    int count = 0;
    String song;
    private static final int NOTIFICATION_ID = 1;
    private int mHour;
    private int mMinute;
    Handler h = new Handler();
    ImageButton back10btn, pausebtn, stopbtn, forward10btn;
    ImageButton playbtn;
    SharedPreferences sf;
    SharedPreferences.Editor se;
    MediaPlayer mediaPlayerClone=new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        songimage2 = findViewById(R.id.songimage);

        back10btn = findViewById(R.id.back10btn);
        playbtn = findViewById(R.id.playbtn);

        forward10btn = findViewById(R.id.forward10btn);
        songname = findViewById(R.id.songname);
        sb = findViewById(R.id.sb);
        currenttime = findViewById(R.id.currenttime);
        duration2 = findViewById(R.id.duration);

        artist = findViewById(R.id.artist_name);

        collectData();

        if (savedInstanceState == null) {
            // Initialize the MediaPlayer object if it's a new instance of the activity
            mediaPlayer = new MediaPlayer();
        } else {
            // Restore the MediaPlayer object from the saved instance state
            mediaPlayer = MediaPlayer.create(this, Uri.parse(musicLink));
        }

        Calendar currentTime = Calendar.getInstance();
        mHour = currentTime.get(Calendar.HOUR_OF_DAY);
        mMinute = currentTime.get(Calendar.MINUTE);
        forward10btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            forward10();
        }
    });
        Intent intent = getIntent();
        String musicName = intent.getStringExtra("song");
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                songname,
                "translationX",
                0f,
                1000f
        );
        animator.setDuration(20000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
        DatabaseReference songsRef = FirebaseDatabase.getInstance().getReference("songs");
        songsRef.orderByChild("musicName").equalTo(musicName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Retrieve artist name, banner link, and music link from Firebase
                        String artistName = Objects.requireNonNull(snapshot.child("artist").getValue()).toString();
                        String bannerLink = Objects.requireNonNull(snapshot.child("bannerLink").getValue()).toString();
                        musicLink = Objects.requireNonNull(snapshot.child("musicLink").getValue()).toString();
                        artist.setText(Objects.requireNonNull(snapshot.child("artist").getValue()).toString());
                        songname.setText(Objects.requireNonNull(snapshot.child("musicName").getValue()).toString());

                        Picasso.get().load(bannerLink).into(songimage2);

                        // Prepare and play the music
                        try {
                            mediaPlayer.setDataSource(musicLink);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    int duration = mediaPlayer.getDuration();
                                    sb.setMax(mediaPlayer.getDuration());
                                    duration2.setText(formatTime(mediaPlayer.getDuration()));
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Music Not Found", Toast.LENGTH_LONG).show();
                    finish(); // Finish the activity if music not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load music details", Toast.LENGTH_SHORT).show();
            }
        });


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                currenttime.setText(formatTime(mediaPlayer.getCurrentPosition()));

                if(formatTime(mediaPlayer.getCurrentPosition()).equals(formatTime(mediaPlayer.getDuration()-1000))){
                            forward10();
                    }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateProgress();

    }//end of onCreate()


    public void updateProgress() {
        if (mediaPlayer != null)
            sb.setProgress(mediaPlayer.getCurrentPosition());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null)
                    if (!mediaPlayer.isPlaying()) {
                        playbtn.setImageResource(R.drawable.playbtn);
                    }
                updateProgress();

            }
        }, 1000);
    }

    public void onBackPressed() {
        if (backcount == 1) {
            super.onBackPressed();
            mediaPlayer.pause();

        } else {
            Toast.makeText(getApplicationContext(), "Press Again To Exit", Toast.LENGTH_SHORT).show();
            backcount++;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void playsong(View v) {
        if (!mediaPlayer.isPlaying()) {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.start();

                } else {
                    mediaPlayer.pause();

                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error While Playing Song", Toast.LENGTH_LONG).show();
                Log.d("Error Roshan", e.toString());
            }
            playbtn.setImageResource(R.drawable.pausebtn);

        } else {
            mediaPlayer.pause();

            playbtn.setImageResource(R.drawable.playbtn);

        }
        count++;
    }




    public void forward10() {
        // Play a random song from the list
        traverseSongDataList(songDataList);
    }

    public void backward10(View v) {
        Picasso.get().load(pastbannerLink).into(songimage2);
        songname.setText(pastartist);
        artist.setText(pastmusicName);


        try {

            // Reset the MediaPlayer to the Initialized state
            mediaPlayer.reset();

            // Set the data source for the MediaPlayer
            mediaPlayer.setDataSource(pastmusicLink);

            // Prepare the MediaPlayer asynchronously
            mediaPlayer.prepareAsync();

            // Set a listener for when the MediaPlayer is prepared
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // MediaPlayer is prepared, you can start playback or perform other operations
                    int duration = mediaPlayer.getDuration();
                    sb.setMax(duration);
                    duration2.setText(formatTime(duration));
                    // Start playback if needed
                    mediaPlayer.start();
                    playbtn.setImageResource(R.drawable.pausebtn);
                }
            });
        } catch (IOException e) {
            // Handle IOException (e.g., invalid data source)
            e.printStackTrace();
        }
    }


    private String formatTime(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public void goback(View v) {
        if (backcount == 1) {
            super.onBackPressed();
            mediaPlayer.pause();
        } else {
            Toast.makeText(getApplicationContext(), "Press Again To Exit", Toast.LENGTH_SHORT).show();
            backcount++;
        }
    }

    public void showSleepTimerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        startTimer();
                    }
                }, hour, minute, false);


        timePickerDialog.show();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Get the current time
                Calendar currentTime = Calendar.getInstance();
                int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                int currentMinute = currentTime.get(Calendar.MINUTE);

                // Compare the current time with the time set by the user
                if (currentHour == mHour && currentMinute == mMinute) {
                    // Do something when the current time matches the set time
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.pause();

                }
            }
        }, 0, 60 * 1000); // Check every minute
    }

    public void showTimerDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sleep Timer")
                .setMessage("")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform set timer action
                        showSleepTimerDialog();
                        // Call method to set timer
                        // Example: startTimer();
                    }
                })
                .setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform clear timer action
                        if (timer != null)
                            timer.cancel();
                        // Call method to clear timer
                        // Example: stopTimer();
                    }
                })
                .setCancelable(true); // allow dismissing the dialog by tapping outside

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void collectData() {
        DatabaseReference songsRef = FirebaseDatabase.getInstance().getReference("songs");
        songsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    currentartist = snapshot.child("artist").getValue(String.class);
                    currentmusicName = snapshot.child("musicName").getValue(String.class);
                    currentmusicLink = snapshot.child("musicLink").getValue(String.class);
                    currentbannerLink = snapshot.child("bannerLink").getValue(String.class);
                    songDataList.add(new Song(currentartist, currentmusicName, currentmusicLink, currentbannerLink));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load music details", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void traverseSongDataList(List<Song> songDataList) {
        if (!songDataList.isEmpty()) {

            pastartist=currentartist;
            pastbannerLink=currentbannerLink;
            pastmusicLink=currentmusicLink;
            pastmusicName=currentmusicName;

            Random random = new Random();
            int randomIndex = random.nextInt(songDataList.size());
            Song randomSong = songDataList.get(randomIndex);

            currentartist = randomSong.getArtist();
            currentmusicName = randomSong.getMusicName();
            currentmusicLink = randomSong.getMusicLink();
            currentbannerLink = randomSong.getBannerLink();

            Picasso.get().load(currentbannerLink).into(songimage2);
            songname.setText(currentartist);
            artist.setText(currentmusicName);


            try {

                // Reset the MediaPlayer to the Initialized state
                mediaPlayer.reset();

                // Set the data source for the MediaPlayer
                mediaPlayer.setDataSource(currentmusicLink);

                // Prepare the MediaPlayer asynchronously
                mediaPlayer.prepareAsync();

                // Set a listener for when the MediaPlayer is prepared
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // MediaPlayer is prepared, you can start playback or perform other operations
                        int duration = mediaPlayer.getDuration();
                        sb.setMax(duration);
                        duration2.setText(formatTime(duration));
                        // Start playback if needed
                        mediaPlayer.start();
                        playbtn.setImageResource(R.drawable.pausebtn);
                    }
                });
            } catch (IOException e) {
                // Handle IOException (e.g., invalid data source)
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Music Not Found", Toast.LENGTH_LONG).show();
        }
    }



    public void next10(View v){
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+1000*10);
    }
    public void previous10(View v){
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-1000*10);
    }

}
