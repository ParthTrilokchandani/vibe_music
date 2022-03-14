package com.example.charo_taraf_vibe.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.example.charo_taraf_vibe.PlaylistDisplay
import com.example.charo_taraf_vibe.R
import com.example.database.MyDatabaseHelper
import com.example.database.models.Playlist
import com.example.database.models.PlaylistItems

class PlaylistAdapter(
    private val activity: Activity,
    private val objects: Array<Playlist>
) : ArrayAdapter<Playlist>(activity, R.layout.activity_playlist, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        try {
            val row = activity.layoutInflater.inflate(R.layout.playlist_grid, parent, false)
            val imgProduct = row.findViewById<Button>(R.id.playlist)
            val lblPlaylistName = row.findViewById<TextView>(R.id.lblPlaylistName)
            val lblSongCount = row.findViewById<TextView>(R.id.lblSongCount)

            lblPlaylistName.text = objects[position].name
            lblSongCount.setText("${objects[position].songCount.toString()} Songs")

            val card = row.findViewById<CardView>(R.id.playlistId)
            card.setOnClickListener{

                val songPathToAdd = activity.intent.getStringExtra("songToAddPath")
                val songPathToName = activity.intent.getStringExtra("songToAddName")
                if(songPathToAdd.equals(null) && songPathToName.equals(null)){

                        val intent = Intent(context, PlaylistDisplay::class.java)
                        intent.putExtra("playlistId", objects[position].id.toString())
                        context.startActivity(intent)
                }
                else
                {
                    MyDatabaseHelper(context).insertPlaylistItems(PlaylistItems(null,objects[position].id?.toLong(),songPathToName!!,songPathToAdd!!))
                    Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
                    activity.finish()
                }
            }
            return row
        } catch (ex: Exception) {
            Log.e("inf", ex.message!!)
            return convertView!!

        }
    }
}