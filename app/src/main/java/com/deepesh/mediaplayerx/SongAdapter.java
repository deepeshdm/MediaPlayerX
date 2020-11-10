package com.deepesh.mediaplayerx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {


    List<Song> songList;
    OnSongClickListener songClickListener;

    public SongAdapter(List<Song> songList, OnSongClickListener listener) {
        this.songList = songList;
        this.songClickListener=listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_song,parent,false);
       return new SongViewHolder(view,songClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {

        Song currentSong = songList.get(position);
        holder.song_name_tv.setText(currentSong.getName());

        holder.song_name_tv.setOnClickListener(v -> songClickListener.OnSongClick(position));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder{

        TextView song_name_tv;
        OnSongClickListener songClickListener;

        public SongViewHolder(@NonNull View itemView, OnSongClickListener listener) {
            super(itemView);
            song_name_tv=itemView.findViewById(R.id.song_name);
            this.songClickListener=listener;

            itemView.setOnClickListener(v -> songClickListener.OnSongClick(getAdapterPosition()));
        }

    }


    public interface OnSongClickListener{
        void OnSongClick(int position);
    }

}
