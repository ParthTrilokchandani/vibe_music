package com.example.charo_taraf_vibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.charo_taraf_vibe.adapter.PlaylistAdapter
import com.example.database.MyDatabaseHelper
import com.example.database.models.Playlist

class PlaylistActivity : AppCompatActivity() {

    private lateinit var playlistCard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        val grdPlaylist = findViewById<GridView>(R.id.grdPlaylist)

        val playlistArray = arrayOf<Playlist>()
        val playlists = MyDatabaseHelper(this).getPlaylist().toArray(playlistArray)

        val adapter = PlaylistAdapter(this, playlists)
        grdPlaylist.adapter = adapter

    }
}