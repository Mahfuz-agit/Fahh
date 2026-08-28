package com.example.bgrecorder

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

data class RecordingItem(
    val file: File,
    val name: String,
    val sizeKb: Long,
    val date: String
)

object RecordingUtils {

    fun listRecordings(context: Context): List<RecordingItem> {
        val dir = context.getExternalFilesDir(null) ?: return emptyList()
        val files = dir.listFiles { f -> f.extension == "m4a" } ?: emptyArray()
        val fmt = SimpleDateFormat("MMM dd, HH:mm", Locale.US)
        return files
            .sortedByDescending { it.lastModified() }
            .map {
                RecordingItem(
                    file = it,
                    name = it.name,
                    sizeKb = it.length() / 1024,
                    date = fmt.format(it.lastModified())
                )
            }
    }
}
