package com.anix.app.core.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileInputStream

fun downloadVideoMp4(context: Context, videoUrl: String, fileName: String) {
    Toast.makeText(context, "Sedang didownload, sabar ya~", Toast.LENGTH_SHORT).show()

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = Uri.parse(videoUrl)

    val request = DownloadManager.Request(uri).apply {
        setTitle("Mengunduh Video")
        setDescription("Menyiapkan file $fileName...")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setMimeType("video/mp4")
        setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, "$fileName.mp4")
    }

    val downloadId = downloadManager.enqueue(request)

    val onComplete = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                pindahkanKeFolderDownloadPublik(context, fileName)
                try {
                    context.unregisterReceiver(this)
                } catch (_: Exception) {}
            }
        }
    }
    context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
}

private fun pindahkanKeFolderDownloadPublik(context: Context, fileName: String) {
    val folderPrivat = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
    val fileSumber = File(folderPrivat, "$fileName.mp4")
    if (!fileSumber.exists()) return

    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.mp4")
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
    }

    val koleksiUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Downloads.EXTERNAL_CONTENT_URI
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val uriTujuan = resolver.insert(koleksiUri, contentValues)
    uriTujuan?.let { uri ->
        resolver.openOutputStream(uri)?.use { outputStream ->
            FileInputStream(fileSumber).use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        fileSumber.delete()
    }
}
