package com.roshtune;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {


    GridView gv;
    EditText search;
    SongAdapter adapter;
    List<String> musicNames,bannerLinks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference songsRef = database.getReference("songs");

        gv=findViewById(R.id.music_grid);
        search=findViewById(R.id.search_bar);

        search.addTextChangedListener(new TextWatcher() {
                                          @Override
                                          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                          }

                                          @Override
                                          public void onTextChanged(CharSequence s, int start, int before, int count) {

                                          }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().toLowerCase().trim();

                // Create lists to hold filtered songs
                List<String> filteredMusicNames = new ArrayList<>();
                List<String> filteredBannerLinks = new ArrayList<>();

                // If search text is empty, display all songs
                if (searchText.isEmpty()) {
                    filteredMusicNames.addAll(musicNames);
                    filteredBannerLinks.addAll(bannerLinks);
                } else {
                    // Iterate over all songs to find matches
                    for (int i = 0; i < musicNames.size(); i++) {
                        String musicName = musicNames.get(i).toLowerCase();
                        if (musicName.contains(searchText)) {
                            // Add matching song to filtered list
                            filteredMusicNames.add(musicNames.get(i));
                            filteredBannerLinks.add(bannerLinks.get(i));
                        }
                    }
                }

                // Update the adapter with filtered results

                gv.setAdapter(new CustomAdapter(filteredMusicNames, filteredBannerLinks));
            }
                                      });


                songsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Song> songsList = new ArrayList<>();
                        musicNames = new ArrayList<>();
                        bannerLinks = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String musicName = snapshot.child("musicName").getValue(String.class);
                            String bannerLink = snapshot.child("bannerLink").getValue(String.class);

                            musicNames.add(musicName);
                            bannerLinks.add(bannerLink);


                            Song song = snapshot.getValue(Song.class);
                            songsList.add(song);

                            // Add Song object directly to ArrayList
                        }
                        // Pass songsList to adapter
                        adapter = new SongAdapter(getApplicationContext(), songsList);
                        gv.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the TextView from the clicked grid item
                TextView textView = view.findViewById(R.id.textViewMusicName);
                // Get the text from the TextView
                String musicName = textView.getText().toString();

                // Start a new Intent and pass the music name as an extra
                Intent intent = new Intent(getApplicationContext(), MusicPlayer.class);
                intent.putExtra("song", musicName);
                startActivity(intent);
            }
        });
    }

        public void addsongintent(View v) {
            startActivity(new Intent(getApplicationContext(), AddSong.class));

        }






    //inner class

    public class CustomAdapter extends BaseAdapter {

        List<String> names2;
        List<String> images2;

        public CustomAdapter(List<String> namesarr, List<String> imagesarray) {
            names2 = namesarr;
            images2 = imagesarray;
        }

        @Override
        public int getCount() {
            return names2.size();
        }

        @Override
        public Object getItem(int position) {
            return names2.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View v = inflater.inflate(R.layout.grid_item_song, parent, false);
            ImageView iv = v.findViewById(R.id.imageViewBanner);
            TextView tv = v.findViewById(R.id.textViewMusicName);
            Picasso.get().load(images2.get(position)).into(iv);
            tv.setTextColor(Color.BLACK);
            tv.setText(names2.get(position));
            return v;
        }
    }


    public void openProfile(View c){
        startActivity(new Intent(getApplicationContext(), UserProfile.class));
    }


}