package com.example.charo_taraf_vibe.adapter

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.charo_taraf_vibe.R
import com.example.database.models.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PlaylistAdapter(
    private val activity: Activity,
    private val objects: Array<Playlist>
) : ArrayAdapter<Playlist>(activity, R.layout.activity_playlist, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        try {
            val row = activity.layoutInflater.inflate(R.layout.playlist_grid, parent, false)
            val imgProduct = row.findViewById<ImageView>(R.id.imgProduct)
            val lblPlaylistName = row.findViewById<TextView>(R.id.lblPlaylistName)
            val lblSongCount = row.findViewById<TextView>(R.id.lblSongCount)

            lblPlaylistName.text = objects[position].name
            lblSongCount.text = "12"

            return row
        } catch (ex: Exception) {
            Log.e("inf", ex.message!!)
            return convertView!!

        }
    }
}