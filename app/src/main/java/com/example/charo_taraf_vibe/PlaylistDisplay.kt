package com.example.charo_taraf_vibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.database.MyDatabaseHelper

class PlaylistDisplay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_display)

        val playlistId = intent.getStringExtra("playlistId")
        val playlistItems = MyDatabaseHelper(this).getPlaylistItems(playlistId!!.toInt())

        val mp3Names = arrayListOf<String>()
        val mp3Path = arrayListOf<String>()
        playlistItems.map {
            mp3Names.add(it.songname)
            mp3Path.add(it.songpath)
        }

        val musicPlaylistView = findViewById<ListView>(R.id.musicPlaylistLV)
        val musicArrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, mp3Names)
        musicPlaylistView.adapter = musicArrayAdapter

        musicPlaylistView.setOnItemClickListener { _: AdapterView<*>, _: View, index: Int, _: Long ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("mp3Path", mp3Path[index])
            intent.putExtra("mp3Name", mp3Names[index])
            intent.putExtra("musicId", "${index}")
            startActivity(intent)
        }
    }
}