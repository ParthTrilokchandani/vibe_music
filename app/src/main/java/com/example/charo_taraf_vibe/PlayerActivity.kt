package com.example.charo_taraf_vibe

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.database.MyDatabaseHelper
import com.example.database.models.PlaylistItems
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*

class PlayerActivity : AppCompatActivity()
{
    private lateinit var songName: TextView
    private lateinit var mPlayButton: MaterialButton
    private lateinit var currentDuration: TextView
    private lateinit var totalDuration: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var playBack: Button
    private lateinit var playForward: Button
    private var musicId : Int = -1
    private lateinit var like: MaterialButton

    private lateinit var seekBarUpdateJob: Job

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        supportActionBar?.hide()

        songName = findViewById(R.id.songName)
        songName.isSelected = true

        val back = findViewById<Button>(R.id.back).setOnClickListener {
            moveTaskToBack(true)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        seekBar = findViewById(R.id.play_seekbar)
        mPlayButton = findViewById(R.id.play)
        totalDuration = findViewById(R.id.totalDuration)
        currentDuration = findViewById(R.id.currentDuration)
        playBack = findViewById(R.id.playBack)
        playForward = findViewById(R.id.playForward)
        like = findViewById(R.id.like)

        like.setOnClickListener{
            if(it.tag.equals("liked")) {
                MyDatabaseHelper(this).deletePlaylistItems(Shared.currentMp3Name.toString())
                like.setIconResource(R.drawable.ic_heart_outline)
                like.tag = "notliked"
            } else {
                MyDatabaseHelper(this).insertPlaylistItems(playlistItems = PlaylistItems(null, 1, Shared.currentMp3Name.toString(),Shared.currentMp3.toString()))
                like.setIconResource(R.drawable.ic_heart_fill)
                like.tag = "liked"
            }
        }
        //play back-forward start

        playBack.setOnClickListener{
            val prefs = getSharedPreferences("MusicList", MODE_PRIVATE)
            val list = prefs.getString("list", "default")
            val mapList = Shared.convertToHashMap(list)

            Shared.currentMp3 = "${mapList[musicId-1]["path"]}/${mapList[musicId-1]["name"]}"
            Shared.currentMp3Name = "${mapList[musicId-1]["name"]}"
            Shared.currentProgress = 0

            Shared.currentMediaPlayer?.pause()
            Shared.currentMediaPlayer?.release()
            Shared.currentMediaPlayer = null


            Shared.currentMediaPlayer = MediaPlayer.create(this, Uri.parse("${mapList[musicId-1]["path"]}/${mapList[musicId-1]["name"]}"))

            songName.text = mapList[musicId-1]["name"]
            totalDuration.text = Shared.convertToMinutes(Shared.currentMediaPlayer?.duration!!)
            currentDuration.text = resources.getString(R.string.start_music)

            seekBar.progress = 0
            seekBar.max = Shared.currentMediaPlayer?.duration!!
            mPlayButton.setIconResource(R.drawable.ic_pause)
            checkSongIsLiked()

            Shared.currentMediaPlayer?.start()
            musicId = musicId -1
        }

        playForward.setOnClickListener{
            val prefs = getSharedPreferences("MusicList", MODE_PRIVATE)
            val list = prefs.getString("list", "default")
            val mapList = Shared.convertToHashMap(list)

            Shared.currentMp3 = "${mapList[musicId+1]["path"]}/${mapList[musicId+1]["name"]}"
            Shared.currentMp3Name = "${mapList[musicId+1]["name"]}"
            Shared.currentProgress = 0

            Shared.currentMediaPlayer?.pause()
            Shared.currentMediaPlayer?.release()
            Shared.currentMediaPlayer = null

            Shared.currentMediaPlayer = MediaPlayer.create(this, Uri.parse("${mapList[musicId+1]["path"]}/${mapList[musicId+1]["name"]}"))

            songName.text = mapList[musicId+1]["name"]
            totalDuration.text = Shared.convertToMinutes(Shared.currentMediaPlayer?.duration!!)
            currentDuration.text = resources.getString(R.string.start_music)

            seekBar.progress = 0
            seekBar.max = Shared.currentMediaPlayer?.duration!!

            mPlayButton.setIconResource(R.drawable.ic_pause)
            Shared.currentMediaPlayer?.start()
            checkSongIsLiked()
            musicId = musicId + 1
        }

        //play back-forward end

        mPlayButton.setIconResource(R.drawable.ic_pause)

        mPlayButton.setOnClickListener {
            if (!Shared.currentMediaPlayer?.isPlaying!!)
            {
                Shared.currentMediaPlayer?.start()
                mPlayButton.setIconResource(R.drawable.ic_pause)
            }
            else
            {
                Shared.currentMediaPlayer?.pause()
                mPlayButton.setIconResource(R.drawable.ic_play)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean)
            {
                currentDuration.text = Shared.convertToMinutes(p1)
                if (p2)
                {
                    Shared.currentMediaPlayer?.seekTo(p1)
                    Shared.currentProgress = p1
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?)
            {

            }

            override fun onStopTrackingTouch(p0: SeekBar?)
            {

            }
        })

        Shared.currentMediaPlayer?.setOnCompletionListener {
            Shared.currentMediaPlayer?.pause()
            mPlayButton.setIconResource(R.drawable.ic_play)
        }

        seekBarUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (true)
            {
                delay(1000)
                if (Shared.currentMediaPlayer?.isPlaying!!)
                {
                    seekBar.progress = Shared.currentProgress
                    Shared.currentProgress += 1000
                    currentDuration.text = Shared.convertToMinutes(Shared.currentProgress)
                }
            }
        }
    }

    override fun onResume()
    {
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
        playSelectedMp3()
        checkSongIsLiked()
        super.onResume()
    }

    override fun onPause()
    {
        Toast.makeText(this, "paused", Toast.LENGTH_SHORT).show()
        super.onPause()
    }

    override fun onStop()
    {
        seekBarUpdateJob.cancel()
        super.onStop()
    }

    override fun onDestroy()
    {
        Toast.makeText(this, "paused", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    private fun playSelectedMp3()
    {
        val mp3Path = intent.getStringExtra("mp3Path")
        val mp3Name = intent.getStringExtra("mp3Name")
        musicId = intent.getStringExtra("musicId").toString().toInt()

        if (mp3Path == Shared.currentMp3)
        {
            totalDuration.text = Shared.convertToMinutes(Shared.currentMediaPlayer?.duration!!)
            currentDuration.text = Shared.convertToMinutes(Shared.currentProgress)

            seekBar.progress = Shared.currentProgress
            seekBar.max = Shared.currentMediaPlayer?.duration!!

            return
        }

        Shared.currentMp3 = mp3Path
        Shared.currentMp3Name = mp3Name
        Shared.currentProgress = 0

        if (Shared.currentMediaPlayer != null)
        {
            Shared.currentMediaPlayer?.pause()
            Shared.currentMediaPlayer?.release()
            Shared.currentMediaPlayer = null
        }

        Shared.currentMediaPlayer = MediaPlayer.create(this, Uri.parse(mp3Path))

        songName.text = mp3Name
        totalDuration.text = Shared.convertToMinutes(Shared.currentMediaPlayer?.duration!!)
        currentDuration.text = resources.getString(R.string.start_music)

        seekBar.progress = 0
        seekBar.max = Shared.currentMediaPlayer?.duration!!

        Shared.currentMediaPlayer?.start()

        Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show()
    }

    private fun checkSongIsLiked(){
        val playlistItems = MyDatabaseHelper(this).getPlaylistItems(1)
        playlistItems.map {
            if(it.songname.equals(Shared.currentMp3Name.toString())) {
                like.setIconResource(R.drawable.ic_heart_fill)
                like.tag = "liked"
                return@map
            }
            else {
                like.setIconResource(R.drawable.ic_heart_outline)
                like.tag = "notliked"
            }
        }
    }
}