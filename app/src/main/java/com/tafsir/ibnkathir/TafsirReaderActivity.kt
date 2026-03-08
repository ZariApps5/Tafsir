package com.tafsir.ibnkathir

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.tafsir.ibnkathir.data.Surah
import com.tafsir.ibnkathir.data.SurahRepository
import com.tafsir.ibnkathir.data.Volumes
import com.tafsir.ibnkathir.databinding.ActivityTafsirReaderBinding
import com.tafsir.ibnkathir.util.BookmarkManager
import com.tafsir.ibnkathir.util.PdfDownloadManager

class TafsirReaderActivity : AppCompatActivity(),
    OnLoadCompleteListener, OnPageChangeListener, OnErrorListener {

    private lateinit var binding: ActivityTafsirReaderBinding
    private lateinit var downloadManager: PdfDownloadManager
    private lateinit var bookmarkManager: BookmarkManager

    private lateinit var surah: Surah
    private var currentPage = 0
    private var isNightMode = false
    private var isBookmarked = false

    // Menu items — kept as fields so we can update icons after inflation
    private var menuBookmark: MenuItem? = null
    private var menuNightMode: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTafsirReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        downloadManager  = PdfDownloadManager(this)
        bookmarkManager  = BookmarkManager(this)
        isNightMode      = bookmarkManager.isNightMode

        val surahNumber = intent.getIntExtra(EXTRA_SURAH_NUMBER, 1)
        val resolved    = SurahRepository.getById(surahNumber)
        if (resolved == null) { finish(); return }
        surah = resolved

        isBookmarked = bookmarkManager.isBookmarked(surah.number)

        supportActionBar?.title    = "${surah.number}. ${surah.nameEnglish}"
        supportActionBar?.subtitle = surah.nameArabic

        // Decide which page to open:
        //   savedInstanceState  → user rotated screen, restore exact position
        //   hasLastPage         → returning reader, open where they left off
        //   else                → first visit, open surah start
        val startPage = when {
            savedInstanceState != null ->
                savedInstanceState.getInt(KEY_PAGE, surah.pageStart)
            bookmarkManager.hasLastPage(surah.number) ->
                bookmarkManager.getLastPage(surah.number, surah.pageStart)
            else ->
                surah.pageStart
        }

        loadPdf(startPage)

        setupZoomButtons()
    }

    // ── PDF loading ────────────────────────────────────────────────────────────

    private fun loadPdf(startPage: Int) {
        val volumeInfo = Volumes.forVolume(surah.volume) ?: run {
            Toast.makeText(this, "Volume info missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val pdfFile = downloadManager.getLocalFile(volumeInfo)
        if (!pdfFile.exists()) {
            Toast.makeText(this, "PDF not downloaded. Go back and download it.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        binding.pdfView
            .fromFile(pdfFile)
            .defaultPage(startPage)
            .onLoad(this)
            .onPageChange(this)
            .onError(this)
            .scrollHandle(DefaultScrollHandle(this))
            .spacing(4)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .enableAnnotationRendering(false)
            .nightMode(isNightMode)
            .load()
    }

    // ── Zoom buttons ────────────────────────────────────────────────────────────

    private fun setupZoomButtons() {
        binding.fabZoomIn.setOnClickListener {
            val newZoom = (binding.pdfView.zoom * ZOOM_STEP).coerceAtMost(MAX_ZOOM)
            binding.pdfView.zoomWithAnimation(newZoom)
        }
        binding.fabZoomOut.setOnClickListener {
            val newZoom = (binding.pdfView.zoom / ZOOM_STEP).coerceAtLeast(MIN_ZOOM)
            binding.pdfView.zoomWithAnimation(newZoom)
        }
    }

    // ── PDFView callbacks ──────────────────────────────────────────────────────

    override fun loadComplete(nbPages: Int) {
        binding.progressBar.visibility = View.GONE
        updatePageIndicator()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        currentPage = page
        updatePageIndicator()
        // Auto-save reading position (throttled by the fact this only fires on page change)
        bookmarkManager.saveLastPage(surah.number, page)
    }

    override fun onError(t: Throwable?) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, "Error loading PDF: ${t?.message}", Toast.LENGTH_LONG).show()
    }

    private fun updatePageIndicator() {
        val total = binding.pdfView.pageCount
        binding.tvPageIndicator.text = "Page ${currentPage + 1} / $total"
    }

    // ── Options menu ───────────────────────────────────────────────────────────

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_reader, menu)
        menuBookmark  = menu.findItem(R.id.action_bookmark)
        menuNightMode = menu.findItem(R.id.action_night_mode)
        updateBookmarkIcon()
        updateNightModeIcon()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { onBackPressedDispatcher.onBackPressed(); true }

            R.id.action_bookmark -> {
                isBookmarked = bookmarkManager.toggleBookmark(surah.number)
                updateBookmarkIcon()
                val msg = if (isBookmarked) "Bookmarked ${surah.nameEnglish}" else "Bookmark removed"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_night_mode -> {
                isNightMode = !isNightMode
                bookmarkManager.isNightMode = isNightMode
                updateNightModeIcon()
                // Reload PDF at the same page with new night-mode setting
                loadPdf(binding.pdfView.currentPage)
                true
            }

            R.id.action_jump_to_start -> {
                binding.pdfView.jumpTo(surah.pageStart, true)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateBookmarkIcon() {
        menuBookmark?.icon = ContextCompat.getDrawable(
            this,
            if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline
        )
    }

    private fun updateNightModeIcon() {
        menuNightMode?.icon = ContextCompat.getDrawable(
            this,
            if (isNightMode) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PAGE, binding.pdfView.currentPage)
    }

    companion object {
        const val EXTRA_SURAH_NUMBER = "extra_surah_number"
        private const val KEY_PAGE   = "key_page"
        private const val ZOOM_STEP  = 1.25f
        private const val MIN_ZOOM   = 1.0f
        private const val MAX_ZOOM   = 5.0f
    }
}
