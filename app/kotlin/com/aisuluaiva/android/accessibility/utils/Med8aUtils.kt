package com.aisuluaiva.android.accessibility.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore

class MediaUtils(private val context: Context) {

    fun getAudioFiles(): List<String>? {
        // Check if permission for reading external storage is granted
        if (!PermissionUtils.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return null
        }

        val audioFiles = mutableListOf<String>()
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        
        // Query for audio files in external storage
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val audioFilePath = cursor.getString(dataIndex)
                audioFiles.add(audioFilePath)
            }
        }

        return audioFiles
    }
}