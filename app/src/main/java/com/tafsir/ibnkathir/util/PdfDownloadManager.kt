package com.tafsir.ibnkathir.util

import android.content.Context
import com.tafsir.ibnkathir.data.VolumeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

sealed class DownloadResult {
    data class Success(val file: File) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
    object AlreadyExists : DownloadResult()
}

/**
 * Downloads PDF volumes on-demand and stores them in the app's
 * private files directory (no storage permission required on API 29+).
 */
class PdfDownloadManager(private val context: Context) {

    private val pdfDir: File
        get() = File(context.filesDir, "volumes").also { it.mkdirs() }

    fun getLocalFile(volumeInfo: VolumeInfo): File =
        File(pdfDir, volumeInfo.localFileName)

    fun isDownloaded(volumeInfo: VolumeInfo): Boolean =
        getLocalFile(volumeInfo).exists()

    /**
     * Downloads a volume PDF with progress reporting.
     * Call from a coroutine. [onProgress] delivers 0–100.
     */
    suspend fun download(
        volumeInfo: VolumeInfo,
        onProgress: (Int) -> Unit
    ): DownloadResult = withContext(Dispatchers.IO) {
        val dest = getLocalFile(volumeInfo)
        if (dest.exists()) return@withContext DownloadResult.AlreadyExists

        val tempFile = File(pdfDir, "${volumeInfo.localFileName}.tmp")

        try {
            val connection = URL(volumeInfo.downloadUrl).openConnection() as HttpURLConnection
            connection.apply {
                connectTimeout = 15_000
                readTimeout    = 30_000
                requestMethod  = "GET"
                setRequestProperty("User-Agent", "TafsirApp/1.0")
                connect()
            }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext DownloadResult.Error(
                    "Server returned HTTP ${connection.responseCode}"
                )
            }

            val totalBytes = connection.contentLengthLong
            var downloadedBytes = 0L
            var lastReportedProgress = -1

            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        if (totalBytes > 0) {
                            val progress = ((downloadedBytes * 100) / totalBytes).toInt()
                            if (progress != lastReportedProgress) {
                                lastReportedProgress = progress
                                withContext(Dispatchers.Main) { onProgress(progress) }
                            }
                        }
                    }
                }
            }

            // Atomically rename temp → final
            tempFile.renameTo(dest)
            DownloadResult.Success(dest)

        } catch (e: Exception) {
            tempFile.delete()
            DownloadResult.Error(e.message ?: "Unknown error")
        }
    }

    /** Delete a downloaded volume to free space. */
    fun deleteVolume(volumeInfo: VolumeInfo): Boolean =
        getLocalFile(volumeInfo).delete()

    /** Total MB used by all downloaded volumes. */
    fun totalStorageUsedMb(): Float {
        val totalBytes = pdfDir.listFiles()?.sumOf { it.length() } ?: 0L
        return totalBytes / (1024f * 1024f)
    }
}
