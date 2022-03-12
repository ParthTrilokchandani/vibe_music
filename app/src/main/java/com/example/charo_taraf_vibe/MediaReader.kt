package com.example.charo_taraf_vibe

import android.os.Environment
import java.io.File

class MediaReader {

    fun getAllMp3s(): ArrayList<HashMap<String, String>> {

        val rootExternalDir = Environment.getExternalStorageDirectory()!!

        val listHashMap = arrayListOf<HashMap<String, String>>()
        walk(rootExternalDir, listHashMap, 0)

        return listHashMap
    }

    private fun walk(root: File, list: ArrayList<HashMap<String, String>>, level: Int) {

        val files = root.listFiles { obj -> obj.isFile && (obj.name.endsWith(".mp3")) }
        if (files != null && files.isNotEmpty())
        {
            files.forEach {
                val current: HashMap<String, String> = HashMap()
                current["name"] = it.name
                current["path"] = it.parent!!

                list.add(current)
            }
        }

        val directories = root.listFiles { obj -> obj.isDirectory }
        if (directories != null && directories.isNotEmpty()) {

            for (t in directories) {
                walk(t, list, level + 1)
            }
        }
    }
}