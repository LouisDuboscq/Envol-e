package com.lduboscq.envolee.android

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileWriter

fun convertImageFileToBase64(file: File): String {
    return ByteArrayOutputStream().use { outputStream ->
        Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
            file.inputStream().use { inputStream ->
                inputStream.copyTo(base64FilterStream)
            }
        }
        return@use outputStream.toString()
    }
}

fun shareFileEmail(context: Context, fileName: String, recipient: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
    intent.putExtra(Intent.EXTRA_SUBJECT, "Codes")
    intent.putExtra(
        Intent.EXTRA_TEXT,
        "Imprimez-les codes en pièce jointe et distribuez-les à vos élèves"
    )
    val root = File(context.filesDir, "codes")
    val file = File(root, fileName)
    if (!file.exists() || !file.canRead()) {
        return
    }

    val uri: Uri = FileProvider.getUriForFile(
        context,
        "com.lduboscq.envolee.android.provider",
        file
    )

    // val uri: Uri = Uri.parse("file://" + file.absolutePath)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    context.startActivity(Intent.createChooser(intent, "Send email..."))
}

fun generateNoteOnSD(context: Context, fileName: String, content: String) {
    val root = File(context.filesDir, "codes")
    if (!root.exists()) {
        root.mkdirs()
    }
    val file = File(root, fileName)
    val writer = FileWriter(file)
    writer.append(content)
    writer.flush()
    writer.close()
}

fun MediaPlayer.startPlaying(base64EncodedString: String) {
    try {
        val url = "data:audio/mp3;base64,$base64EncodedString"
        setDataSource(url)
        prepare()
        start()
    } catch (ex: Exception) {
        Log.e("MediaPlayer.startPlaying","startPlaying exception : " + ex.message)
    }
}

class PrepareRecordException : Exception()

fun Long.formatDuration(): String {
    var seconds = this / 1000
    val minutes = seconds / 60
    seconds %= 60
    return String.format("%02d:%02d", minutes, seconds)
}
