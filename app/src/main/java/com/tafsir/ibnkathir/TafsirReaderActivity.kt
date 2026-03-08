package com.tafsir.ibnkathir

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.tafsir.ibnkathir.data.SurahRepository
import com.tafsir.ibnkathir.data.Volumes
import com.tafsir.ibnkathir.databinding.ActivityTafsirReaderBinding
import com.tafsir.ibnkathir.util.PdfDownloadManager

class TafsirReaderActivity : AppCompatActivity(),
    OnLoadCompleteListener, OnPageChangeListener, OnErrorListener {

    private lateinit var binding: ActivityTafsirReaderBinding
    private lateinit var downloadManager: PdfDownloadManager

    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTafsirReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        downloadManager = PdfDownloadManager(this)

        val surahNumber = intent.getIntExtra(EXTRA_SURAH_NUMBER, 1)
        val surah = SurahRepository.getById(surahNumber)

        if (surah == null) {
            Toast.makeText(this, "Surah not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        supportActionBar?.title = "${surah.number}. ${surah.nameEnglish}"
        supportActionBar?.subtitle = surah.nameArabic

        val volumeInfo = Volumes.forVolume(surah.volume)
        if (volumeInfo == null) {
            Toast.makeText(this, "Volume info missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val pdfFile = downloadManager.getLocalFile(volumeInfo)
        if (!pdfFile.exists()) {
            Toast.makeText(this, "PDF not downloaded. Please go back and download it.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        // Restore page position if the user was already reading
        val savedPage = savedInstanceState?.getInt(KEY_PAGE, surah.pageStart) ?: surah.pageStart

        binding.pdfView
            .fromFile(pdfFile)
            .defaultPage(savedPage)
            .onLoad(this)
            .onPageChange(this)
            .onError(this)
            .scrollHandle(DefaultScrollHandle(this))
            .spacing(4)          // dp between pages
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .enableAnnotationRendering(false)
            .load()
    }

    // ── PDFView callbacks ────────────────────────────────────────────────────

    override fun loadComplete(nbPages: Int) {
        binding.progressBar.visibility = View.GONE
        updatePageIndicator()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        currentPage = page
        updatePageIndicator()
    }

    override fun onError(t: Throwable?) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, "Error loading PDF: ${t?.message}", Toast.LENGTH_LONG).show()
    }

    private fun updatePageIndicator() {
        val total = binding.pdfView.pageCount
        binding.tvPageIndicator.text = "Page ${currentPage + 1} / $total"
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PAGE, binding.pdfView.currentPage)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_SURAH_NUMBER = "extra_surah_number"
        private const val KEY_PAGE = "key_page"
    }
}
