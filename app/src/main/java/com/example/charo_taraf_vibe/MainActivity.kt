package com.example.charo_taraf_vibe

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Base64.encode
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.charo_taraf_vibe.adapter.SongListAdapter
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 0
    }
    private lateinit var playlist: Button
    private lateinit var totalSongs: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playlist = findViewById(R.id.playlist)
        totalSongs = findViewById(R.id.totalSongs)
        playlist.setOnClickListener{
            val intent = Intent(this, PlaylistActivity::class.java)
            startActivity(intent)
        }

        if (!checkPermission()) {
            requestPermission()
        }

        val media = MediaReader()
        val prefs = getSharedPreferences("MusicList", MODE_PRIVATE)
        val list = prefs.getString("list", "default")

        var mapList:ArrayList<HashMap<String, String>> = arrayListOf()


        if(list == "default") {
            mapList = media.getAllMp3s()
            var lastElement = mapList.last()

            var data = ""
            for (item in mapList) {
                if(item == lastElement) {
                    val map = item.toMap()
                    data += JSONObject(map).toString()
                } else {
                    val map = item.toMap()
                    data += JSONObject(map).toString() + "@"
                }
            }

            val editor = prefs.edit()
            editor.putString("list", data)
            editor.apply()
        }

        else {
           mapList = Shared.convertToHashMap(list)
        }

        val mp3Names = readAllMp3Files(mapList)
        var mp3NamesArray = arrayOf<String>()
        mp3NamesArray = mp3Names.toTypedArray()
        val musicListView = findViewById<ListView>(R.id.musicLV)
        val musicArrayAdapter: SongListAdapter = SongListAdapter(this, mp3NamesArray)

        musicListView.adapter = musicArrayAdapter
        musicListView.setOnItemClickListener { _: AdapterView<*>, _: View, index: Int, _: Long ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("mp3Path", "${mapList[index]["path"]}/${mapList[index]["name"]}")
            intent.putExtra("mp3Name", "${mapList[index]["name"]}")
            intent.putExtra("musicId", "${index}")
            startActivity(intent)
        }
        totalSongs.setText("Total Songs : ${mp3Names.count()}")
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val result1 = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    // Request Permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data =
                            Uri.parse(String.format("package:%s", applicationContext.packageName))
                        startActivityForResult(intent, 2296)
                    } catch (e: Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        startActivityForResult(intent, 2296)
                    }
                }
            }
        }
    }

    // This function is called when the user accepts or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when the user is prompt for permission.
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setTitle("Permission Required")
                builder1.setMessage("Permission Needed to Access Media Files in your device.")

                builder1.setPositiveButton(
                    "Proceed",
                    DialogInterface.OnClickListener { _, _ ->
                        try {
                            val intent =
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            intent.addCategory("android.intent.category.DEFAULT")
                            intent.data =
                                Uri.parse(
                                    String.format(
                                        "package:%s",
                                        applicationContext.packageName
                                    )
                                )
                            startActivityForResult(intent, 2296)
                        } catch (e: Exception) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                            startActivityForResult(intent, 2296)
                        }
                    }
                )

                builder1.setNegativeButton(
                    "Back"
                ) { dialog, _ -> dialog.cancel() }

                val alert11: AlertDialog = builder1.create()
                alert11.show()
            }
        }
    }
    //permission code over

    private fun readAllMp3Files(mapList:ArrayList<HashMap<String, String>>): ArrayList<String> {

        val mp3Names = arrayListOf<String>()
        for (map in mapList) {
            mp3Names.add(
                map["name"]!!
                    .removeSuffix(".mp3")
                    .removeSuffix(".ogg")
                    .removeSuffix("m4a")
            )
        }
        return mp3Names
    }

}