package com.example.charo_taraf_vibe

import android.content.Intent
import android.media.MediaPlayer
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.coroutines.Job

class Shared {
    companion object {
        var currentMediaPlayer: MediaPlayer? = null
        var currentMp3Path: String? = null
        var currentMp3Name: String? = null
        var currentProgress: Int = 0

        fun convertToMinutes(milliseconds: Int) : String {
            val minutes = milliseconds / 1000 / 60
            val seconds = milliseconds / 1000 % 60

            if(minutes < 9 && seconds < 9) {
                return "0${minutes}:0${seconds}"
            } else if (minutes < 9) {
                return "0${minutes}:${seconds}"
            } else if(seconds < 9) {
                return "${minutes}:0${seconds}"
            } else {
                return "${minutes}:${seconds}"
            }
        }

        fun convertToHashMap(list:String?) : ArrayList<HashMap<String, String>> {
            val temp:ArrayList<HashMap<String, String>> = arrayListOf()
            val musicList = list?.split("@")

            for(music in musicList!!) {
                val removeFirst = music.replace("{", "")
                val removeLast = removeFirst.replace("}", "")
                val removeComma = removeLast.replace("\"", "")
                val removeSlash = removeComma.replace("\\", "")
                val output = removeSlash.split(",")
                    .map { it.split(":") }
                    .map { it.first() to it.last() }
                    .toMap()
                val hash = HashMap(output)
                temp.add(hash)
            }
            return temp
        }
    }
}