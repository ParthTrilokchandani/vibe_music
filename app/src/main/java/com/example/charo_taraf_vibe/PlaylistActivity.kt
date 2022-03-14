package com.example.charo_taraf_vibe

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.constraintlayout.helper.widget.Carousel
import com.example.charo_taraf_vibe.adapter.PlaylistAdapter
import com.example.database.MyDatabaseHelper
import com.example.database.models.Playlist
import com.google.android.material.button.MaterialButton

class PlaylistActivity : AppCompatActivity() {

    private lateinit var addPlaylist: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        addPlaylist = findViewById(R.id.addPlaylist)
        val grdPlaylist = findViewById<GridView>(R.id.grdPlaylist)

        val playlistArray = arrayOf<Playlist>()
        val playlists = MyDatabaseHelper(this).getPlaylist().toArray(playlistArray)

        val adapter = PlaylistAdapter(this, playlists)
        grdPlaylist.adapter = adapter
        addPlaylist.setOnClickListener{
            showDialog(adapter,grdPlaylist)
        }

    }
    private fun showDialog(adapt:PlaylistAdapter,grid:GridView) {
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_add_playlist)
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        dialog.findViewById<MaterialButton>(R.id.check).setOnClickListener{
            MyDatabaseHelper(this).insertPlaylist(Playlist(null,dialog
                .findViewById<EditText>(R.id.playlistName).text.toString(),
                0))
            Toast.makeText(this, "Playlist Created SuccessFully", Toast.LENGTH_SHORT).show()
            adapt.notifyDataSetChanged()
            grid.invalidateViews()
            grid.adapter = adapt
            dialog.hide()

        }
        dialog.findViewById<MaterialButton>(R.id.closeDialog).setOnClickListener{
            dialog.hide()
        }
    }
}