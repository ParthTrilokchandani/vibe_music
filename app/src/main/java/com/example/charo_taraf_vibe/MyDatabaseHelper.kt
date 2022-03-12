package com.example.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.MediaStore
import android.util.Log
import com.example.database.models.Playlist
import com.example.database.models.PlaylistItems

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        @JvmStatic
        val DATABASE_NAME = "vibeMusic"

        @JvmStatic
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        try {

            if (db == null)
                return

            db.execSQL("CREATE TABLE Playlists (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL)")
            db.execSQL("CREATE TABLE PlaylistsItem (Id INTEGER PRIMARY KEY AUTOINCREMENT, PlaylistId LONG NOT NULL, Songname TEXT NOT NULL, Songpath TEXT NOT NULL, FOREIGN KEY(playlistId) REFERENCES Playlist(Id))")
            db.execSQL("INSERT INTO Playlists (Name) VALUES ('LIKED')")

        } catch (ex: SQLiteException) {
            Log.e("MyDatabaseHelper","Database already exists.")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun getPlaylist(): ArrayList<Playlist> {
        val cursor = readableDatabase.query(
            "Playlists",
            arrayOf("*"),
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val arrayOfplaylist = arrayListOf<Playlist>()

        while (!cursor.isAfterLast()) {
            val playlist = Playlist(
                id = cursor.getInt(0),
                name = cursor.getString(1)
            )

            arrayOfplaylist.add(playlist)
            cursor.moveToNext()
        }
        cursor.close()

        return arrayOfplaylist
    }

    fun insertPlaylist(playlist: Playlist): Long {
        val values = ContentValues()
        values.put("Name", playlist.name)

        return writableDatabase.insert("Playlists", null, values)
    }

    fun updatePlaylists(playlist: Playlist, id: Int): Int {
        val values = ContentValues()
        values.put("Name", playlist.name)

        return writableDatabase.update("Playlists", values, "Id = ?", arrayOf(id.toString()))
    }

    fun deletePlaylists(id: Int): Int {
        return writableDatabase.delete("Playlists", "Id = ?", arrayOf(id.toString()))
    }

    //TABLE PLAY LIST ITEMS

    fun insertPlaylistItems(playlistItems: PlaylistItems): Long {
        val values = ContentValues()
        values.put("playlistid", playlistItems.playlistid)
        values.put("songname", playlistItems.songname)
        values.put("songpath", playlistItems.songpath)

        return writableDatabase.insert("PlaylistsItem", null, values)
    }
    fun getPlaylistItems(playlistid: Int): ArrayList<PlaylistItems> {
        val cursor = readableDatabase.query(
            "PlaylistsItem",
            arrayOf("*"),
            "playlistid = ?",
            arrayOf(playlistid.toString()),
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val arrayOfSongs = arrayListOf<PlaylistItems>()

        while (!cursor.isAfterLast()) {
            val playlistItems = PlaylistItems(
                id = cursor.getInt(0),
                playlistid = cursor.getLong(1),
                songname = cursor.getString(2),
                songpath = cursor.getString(3)
            )

            arrayOfSongs.add(playlistItems)
            cursor.moveToNext()
        }
        cursor.close()

        return arrayOfSongs
    }
    fun deletePlaylistItems(songName: String): Int {
        return writableDatabase.delete("PlaylistsItem ", "Songname = ?", arrayOf(songName.toString()))
    }
}