package com.tafsir.ibnkathir.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists two kinds of reading state per Surah:
 *  1. Last-read page  — automatically saved as the user scrolls
 *  2. Bookmarks       — explicitly saved by the user via the toolbar button
 *
 * Also persists the global night-mode preference.
 */
class BookmarkManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Last-read page ────────────────────────────────────────────────────────

    /** Save the current page for a surah (called automatically while reading). */
    fun saveLastPage(surahNumber: Int, page: Int) {
        prefs.edit().putInt(keyLastPage(surahNumber), page).apply()
    }

    /** Returns the last-read page for a surah, or [defaultPage] if none saved. */
    fun getLastPage(surahNumber: Int, defaultPage: Int): Int =
        prefs.getInt(keyLastPage(surahNumber), defaultPage)

    fun hasLastPage(surahNumber: Int): Boolean =
        prefs.contains(keyLastPage(surahNumber))

    // ── Explicit bookmarks ────────────────────────────────────────────────────

    fun isBookmarked(surahNumber: Int): Boolean =
        prefs.getBoolean(keyBookmark(surahNumber), false)

    fun setBookmark(surahNumber: Int, bookmarked: Boolean) {
        prefs.edit().putBoolean(keyBookmark(surahNumber), bookmarked).apply()
    }

    fun toggleBookmark(surahNumber: Int): Boolean {
        val newState = !isBookmarked(surahNumber)
        setBookmark(surahNumber, newState)
        return newState
    }

    /** Returns a set of all bookmarked surah numbers. */
    fun allBookmarkedNumbers(): Set<Int> {
        return (1..114).filter { isBookmarked(it) }.toSet()
    }

    // ── Night mode preference ─────────────────────────────────────────────────

    var isNightMode: Boolean
        get()      = prefs.getBoolean(KEY_NIGHT_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_NIGHT_MODE, value).apply()

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun keyLastPage(surahNumber: Int) = "last_page_$surahNumber"
    private fun keyBookmark(surahNumber: Int)  = "bookmark_$surahNumber"

    companion object {
        private const val PREFS_NAME   = "tafsir_reading_state"
        private const val KEY_NIGHT_MODE = "night_mode"
    }
}
