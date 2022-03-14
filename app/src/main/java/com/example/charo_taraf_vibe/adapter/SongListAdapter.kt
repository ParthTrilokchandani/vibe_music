package com.example.charo_taraf_vibe.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.charo_taraf_vibe.R
import com.example.database.models.Playlist

class SongListAdapter(private val activity: Activity,private val objects: Array<String>)
    : ArrayAdapter<String>(activity, R.layout.activity_main, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = activity.layoutInflater.inflate(R.layout.main_song_list, parent, false)
        row.findViewById<TextView>(R.id.marqueeText).setText(objects[position])
        row.findViewById<TextView>(R.id.marqueeText).isSelected = true
        return row
    }
}
