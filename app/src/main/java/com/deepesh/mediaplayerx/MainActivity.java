package com.deepesh.mediaplayerx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String SONG_NAME_KEY = "song_name";
    public static final String SONG_URL_KEY = "song_url";
    public static final String SONGS_LIST_KEY ="songs_list" ;
    public static final String SONG_POSITION = "song_position";
    RecyclerView song_recyclerview;
    SongAdapter songAdapter;
    List<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = new ArrayList<>();

        // asks a permission to the user.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            String[] permissions ={Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permissions,111);
        }


        LoadSongs();
        songAdapter = new SongAdapter(songList,songClickListener);
        song_recyclerview = findViewById(R.id.recyclerView);
        song_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        song_recyclerview.setAdapter(songAdapter);
    }



// Called when a request permission is denied or accepted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            LoadSongs();
        }else{
            Log.d("TAG", "onRequestPermissionsResult: Requestcode = "+requestCode);
        }
    }

    void LoadSongs() {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, selection, null, MediaStore.Audio.Media.DISPLAY_NAME);

        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String songUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                Song newSong = new Song(name, songUrl);
                songList.add(newSong);
            }
        }
        cursor.close();
    }


    @Override
    protected void onStop() {
        songAdapter.notifyDataSetChanged();
        super.onStop();
    }


    SongAdapter.OnSongClickListener songClickListener = position -> {

        Song currentSong = songList.get(position);

       if (currentSong!=null){
           Intent intent = new Intent(this,music_activity.class);
           intent.putExtra(SONG_NAME_KEY,currentSong.getName());
           intent.putExtra(SONG_URL_KEY,currentSong.getPath());
           intent.putExtra(SONG_POSITION,position);
           intent.putParcelableArrayListExtra(SONGS_LIST_KEY, (ArrayList<? extends Parcelable>) songList);
           startActivity(intent);
       }else{
           Toast.makeText(this, "Failed to Load !", Toast.LENGTH_SHORT).show();
       }
    };


}




















