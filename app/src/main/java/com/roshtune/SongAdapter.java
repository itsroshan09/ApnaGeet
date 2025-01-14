package com.roshtune;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {

    private final Context context;
    private final List<Song> songsList;

    public SongAdapter(Context context, List<Song> songsList) {
        super(context, R.layout.grid_item_song, songsList);
        this.context = context;
        this.songsList = songsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item_song, null);

        ImageView imageViewBanner = view.findViewById(R.id.imageViewBanner);
        TextView textViewMusicName = view.findViewById(R.id.textViewMusicName);

        Song song = songsList.get(position);

        // Set the image to ImageView using Picasso or Glide library
        // You need to load the image from the bannerLink URL
        Picasso.get().load(song.getBannerLink()).into(imageViewBanner);

        // Set the text to TextView
        textViewMusicName.setText(song.getMusicName());

        return view;
    }
}
