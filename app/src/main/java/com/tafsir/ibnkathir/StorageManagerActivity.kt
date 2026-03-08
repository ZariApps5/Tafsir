package com.tafsir.ibnkathir

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tafsir.ibnkathir.data.VolumeInfo
import com.tafsir.ibnkathir.data.Volumes
import com.tafsir.ibnkathir.databinding.ActivityStorageManagerBinding
import com.tafsir.ibnkathir.ui.VolumeDownloadAdapter
import com.tafsir.ibnkathir.ui.VolumeRowState
import com.tafsir.ibnkathir.util.DownloadResult
import com.tafsir.ibnkathir.util.PdfDownloadManager
import kotlinx.coroutines.launch

class StorageManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStorageManagerBinding
    private lateinit var downloadManager: PdfDownloadManager
    private lateinit var adapter: VolumeDownloadAdapter

    // Track which volumes are actively downloading so button states stay correct
    private val downloading = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Storage Manager"

        downloadManager = PdfDownloadManager(this)

        adapter = VolumeDownloadAdapter(
            onDownload = { volume -> confirmDownload(volume) },
            onDelete   = { volume -> confirmDelete(volume) }
        )
        binding.recyclerView.adapter = adapter

        refreshList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // ── List state ─────────────────────────────────────────────────────────────

    private fun refreshList() {
        val rows = Volumes.all.map { info -> rowState(info) }
        adapter.submitList(rows)
        updateSummary(rows)
    }

    private fun rowState(info: VolumeInfo): VolumeRowState {
        val isDownloading = info.volume in downloading
        val file = downloadManager.getLocalFile(info)
        val isDownloaded = !isDownloading && file.exists()

        return VolumeRowState(
            volume           = info.volume,
            title            = info.title,
            surahRange       = info.surahRange,
            status           = when {
                isDownloading -> VolumeRowState.Status.DOWNLOADING
                isDownloaded  -> VolumeRowState.Status.DOWNLOADED
                else          -> VolumeRowState.Status.NOT_DOWNLOADED
            },
            actualSizeMb     = if (isDownloaded) file.length() / (1024f * 1024f) else 0f,
            estimatedSizeMb  = info.estimatedSizeMb
        )
    }

    /** Emit an updated row for a single volume (e.g. download progress tick). */
    private fun updateRow(volume: Int, mutate: VolumeRowState.() -> VolumeRowState) {
        val current = adapter.currentList.toMutableList()
        val idx = current.indexOfFirst { it.volume == volume }
        if (idx == -1) return
        current[idx] = current[idx].mutate()
        adapter.submitList(current)
        updateSummary(current)
    }

    private fun updateSummary(rows: List<VolumeRowState>) {
        val downloaded = rows.count { it.status == VolumeRowState.Status.DOWNLOADED }
        val totalMb    = rows.filter { it.status == VolumeRowState.Status.DOWNLOADED }
                             .sumOf { it.actualSizeMb.toDouble() }
                             .toFloat()
        binding.tvSummary.text =
            "$downloaded of 10 volumes downloaded  ·  ${"%.1f".format(totalMb)} MB used"
    }

    // ── Download ───────────────────────────────────────────────────────────────

    private fun confirmDownload(volume: Int) {
        val info = Volumes.forVolume(volume) ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle("Download ${info.title}?")
            .setMessage("${info.surahRange}\n\nEstimated size: ~${info.estimatedSizeMb} MB")
            .setPositiveButton("Download") { _, _ -> startDownload(info) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startDownload(info: VolumeInfo) {
        downloading += info.volume
        updateRow(info.volume) {
            copy(status = VolumeRowState.Status.DOWNLOADING, downloadProgress = 0)
        }

        lifecycleScope.launch {
            val result = downloadManager.download(info) { progress ->
                updateRow(info.volume) {
                    copy(downloadProgress = progress)
                }
            }
            downloading -= info.volume

            when (result) {
                is DownloadResult.Success,
                DownloadResult.AlreadyExists -> {
                    val file = downloadManager.getLocalFile(info)
                    updateRow(info.volume) {
                        copy(
                            status       = VolumeRowState.Status.DOWNLOADED,
                            actualSizeMb = file.length() / (1024f * 1024f)
                        )
                    }
                }
                is DownloadResult.Error -> {
                    updateRow(info.volume) {
                        copy(status = VolumeRowState.Status.NOT_DOWNLOADED)
                    }
                    Toast.makeText(
                        this@StorageManagerActivity,
                        "Download failed: ${result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    private fun confirmDelete(volume: Int) {
        val info = Volumes.forVolume(volume) ?: return
        val file = downloadManager.getLocalFile(info)
        val sizeMb = "%.1f".format(file.length() / (1024f * 1024f))

        MaterialAlertDialogBuilder(this)
            .setTitle("Delete ${info.title}?")
            .setMessage("This will free $sizeMb MB. You can re-download it anytime.")
            .setPositiveButton("Delete") { _, _ ->
                downloadManager.deleteVolume(info)
                updateRow(volume) {
                    copy(status = VolumeRowState.Status.NOT_DOWNLOADED, actualSizeMb = 0f)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
