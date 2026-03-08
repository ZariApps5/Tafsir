package com.tafsir.ibnkathir.data

/**
 * Represents a Surah with its location in the Tafsir Ibn Kathir
 * (Darussalam English 10-volume edition).
 *
 * [volume]    - PDF volume number (1–10)
 * [pageStart] - Starting page in that volume's PDF (0-indexed for the viewer)
 * [pageEnd]   - Ending page in that volume's PDF (-1 means read to end of Surah section)
 */
data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameMeaning: String,
    val volume: Int,
    val pageStart: Int,    // 0-indexed page within the volume PDF
    val pageEnd: Int = -1  // -1 = next surah's start - 1
)
