package com.tafsir.ibnkathir

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tafsir.ibnkathir.data.Surah
import com.tafsir.ibnkathir.data.SurahRepository
import com.tafsir.ibnkathir.data.Volumes
import com.tafsir.ibnkathir.databinding.ActivityMainBinding
import com.tafsir.ibnkathir.ui.SurahAdapter
import com.tafsir.ibnkathir.util.BookmarkManager
import com.tafsir.ibnkathir.util.DownloadResult
import com.tafsir.ibnkathir.util.PdfDownloadManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SurahAdapter
    private lateinit var downloadManager: PdfDownloadManager
    private lateinit var bookmarkManager: BookmarkManager

    // Track current query so onResume can refresh bookmark icons without losing search
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        downloadManager = PdfDownloadManager(this)
        bookmarkManager = BookmarkManager(this)

        setupRecyclerView()
        setupSearch()
    }

    override fun onResume() {
        super.onResume()
        // Refresh list so bookmark icons stay in sync after returning from reader
        refreshList(currentQuery)
    }

    private fun setupRecyclerView() {
        adapter = SurahAdapter { surah -> onSurahSelected(surah) }
        binding.recyclerView.adapter = adapter
        refreshList("")
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                refreshList(currentQuery)
                return true
            }
        })
    }

    private fun refreshList(query: String) {
        val bookmarked = bookmarkManager.allBookmarkedNumbers()
        if (query.isBlank()) {
            // Full list with volume headers
            adapter.submitSurahsWithHeaders(SurahRepository.surahs, bookmarked)
        } else {
            // Flat search results, no headers
            adapter.submitSurahs(SurahRepository.search(query), bookmarked)
        }
    }

    /**
     * Called when the user taps a Surah. Checks if the volume PDF is
     * downloaded; if not, prompts to download it first.
     */
    private fun onSurahSelected(surah: Surah) {
        val volumeInfo = Volumes.forVolume(surah.volume) ?: return

        if (downloadManager.isDownloaded(volumeInfo)) {
            openReader(surah)
        } else {
            showDownloadDialog(surah)
        }
    }

    private fun showDownloadDialog(surah: Surah) {
        val volumeInfo = Volumes.forVolume(surah.volume) ?: return

        MaterialAlertDialogBuilder(this)
            .setTitle("Download Required")
            .setMessage(
                "To read ${surah.nameEnglish}, you need to download " +
                "${volumeInfo.title} (${volumeInfo.surahRange}).\n\n" +
                "This may use 30–50 MB of data and storage."
            )
            .setPositiveButton("Download") { _, _ -> startDownload(surah) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startDownload(surah: Surah) {
        val volumeInfo = Volumes.forVolume(surah.volume) ?: return

        @Suppress("DEPRECATION")
        val progressDialog = android.app.ProgressDialog(this).apply {
            setTitle("Downloading ${volumeInfo.title}")
            setMessage("Please wait…")
            setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL)
            max = 100
            isIndeterminate = false
            setCancelable(false)
            show()
        }

        lifecycleScope.launch {
            val result = downloadManager.download(volumeInfo) { progress ->
                progressDialog.progress = progress
            }
            progressDialog.dismiss()

            when (result) {
                is DownloadResult.Success,
                DownloadResult.AlreadyExists -> openReader(surah)
                is DownloadResult.Error -> Toast.makeText(
                    this@MainActivity,
                    "Download failed: ${result.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openReader(surah: Surah) {
        startActivity(
            Intent(this, TafsirReaderActivity::class.java).apply {
                putExtra(TafsirReaderActivity.EXTRA_SURAH_NUMBER, surah.number)
            }
        )
    }
}
