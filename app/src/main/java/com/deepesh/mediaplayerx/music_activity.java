package com.deepesh.mediaplayerx;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class music_activity extends AppCompatActivity {

    private static final String TAG = "TAG";
    TextView song_name_tv;
    ImageButton previous_button, play_button, next_button;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Boolean isPlaying;
    List<Song> songList;
    int songPosition;
    Handler seekbarHandler;
    Runnable seekbarRunnable;
    Boolean fromUser;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity);

        songList = new ArrayList<>();
        song_name_tv = findViewById(R.id.song_name_musicTv);
        previous_button = findViewById(R.id.previous_button);
        play_button = findViewById(R.id.play_button);
        next_button = findViewById(R.id.next_button);
        seekBar = findViewById(R.id.seekBar1);
        mediaPlayer = new MediaPlayer();

        seekbarHandler = new Handler();
        seekbarRunnable = () -> {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekbarHandler.postDelayed(seekbarRunnable, 50);
        };

        Intent intent = getIntent();
        String song_name = intent.getStringExtra(MainActivity.SONG_NAME_KEY);
        String song_url = intent.getStringExtra(MainActivity.SONG_URL_KEY);
        songPosition = intent.getIntExtra(MainActivity.SONG_POSITION, 0);
        songList = intent.getParcelableArrayListExtra(MainActivity.SONGS_LIST_KEY);

        song_name_tv.setText(song_name);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);


        // Plays song
        playSong(song_url);

        // when user clicks on the play button.
        play_button.setOnClickListener(v -> {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                // Stop the Handler to stop updating seekbar
                seekbarHandler.removeCallbacks(seekbarRunnable);
                play_button.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                isPlaying = true;
                play_button.setImageResource(R.drawable.ic_pause);
            }
        });

        // when user clicks on next button
        next_button.setOnClickListener(v -> {
            songPosition = songPosition + 1;
            Song song = songList.get(songPosition);
            mediaPlayer.reset();
            playSong(song.getPath());
            song_name_tv.setText(song.getName());
            seekBar.setMin(0);
            seekBar.setMax(mediaPlayer.getDuration());
        });


        // when user clicks on previous button
        previous_button.setOnClickListener(v -> {
            songPosition = songPosition - 1;
            Song song = songList.get(songPosition);
            mediaPlayer.reset();
            playSong(song.getPath());
            song_name_tv.setText(song.getName());
            seekBar.setMin(0);
            seekBar.setMax(mediaPlayer.getDuration());
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    void playSong(String song_url) {

        // Starts the Handler to keep updating seekbar every given seconds.
        seekbarHandler.postDelayed(seekbarRunnable, 0);

        try {
            mediaPlayer.setDataSource(song_url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setMin(0);
            play_button.setImageResource(R.drawable.ic_pause);
        } catch (Exception e) {
            Log.d(TAG, "onCreate:ERROR : " + e.getMessage());
        }
    }


    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
           if (fromUser){
               mediaPlayer.seekTo(progress);
           }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            fromUser=true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            fromUser=false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // releasing resources to avoid memory leaks
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}