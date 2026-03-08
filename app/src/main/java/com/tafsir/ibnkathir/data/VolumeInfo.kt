package com.tafsir.ibnkathir.data

/**
 * Metadata for each of the 10 PDF volumes of Tafsir Ibn Kathir
 * (Darussalam English edition, publicly available on Archive.org).
 *
 * The Archive.org identifiers below point to the freely hosted scans.
 * URL format: https://archive.org/download/{archiveId}/{fileName}
 */
data class VolumeInfo(
    val volume: Int,
    val title: String,
    val archiveId: String,
    val fileName: String,
    val surahRange: String  // Human-readable, e.g. "Al-Fatiha – Al-Baqarah"
) {
    val downloadUrl: String
        get() = "https://archive.org/download/$archiveId/$fileName"

    val localFileName: String
        get() = "tafsir_ibn_kathir_vol_$volume.pdf"
}

object Volumes {
    /**
     * 10-volume Darussalam English edition of Tafsir Ibn Kathir.
     * Archive.org collection: "TafsirIbnKathir-English-10Vols"
     *
     * NOTE: These Archive.org identifiers reference the publicly available,
     * open-access scans of the book. Verify URLs before release.
     */
    val all = listOf(
        VolumeInfo(
            volume = 1,
            title = "Volume 1",
            archiveId = "TafsirIbnKathirVol1",
            fileName = "Tafsir.Ibn.Kathir.Vol.1.pdf",
            surahRange = "Al-Fatiha (1) – Al-Baqarah (2)"
        ),
        VolumeInfo(
            volume = 2,
            title = "Volume 2",
            archiveId = "TafsirIbnKathirVol2",
            fileName = "Tafsir.Ibn.Kathir.Vol.2.pdf",
            surahRange = "Al-Baqarah cont. – Al-Imran (3)"
        ),
        VolumeInfo(
            volume = 3,
            title = "Volume 3",
            archiveId = "TafsirIbnKathirVol3",
            fileName = "Tafsir.Ibn.Kathir.Vol.3.pdf",
            surahRange = "An-Nisa (4) – Al-Maidah (5)"
        ),
        VolumeInfo(
            volume = 4,
            title = "Volume 4",
            archiveId = "TafsirIbnKathirVol4",
            fileName = "Tafsir.Ibn.Kathir.Vol.4.pdf",
            surahRange = "Al-An'am (6) – Al-A'raf (7)"
        ),
        VolumeInfo(
            volume = 5,
            title = "Volume 5",
            archiveId = "TafsirIbnKathirVol5",
            fileName = "Tafsir.Ibn.Kathir.Vol.5.pdf",
            surahRange = "Al-Anfal (8) – Hud (11)"
        ),
        VolumeInfo(
            volume = 6,
            title = "Volume 6",
            archiveId = "TafsirIbnKathirVol6",
            fileName = "Tafsir.Ibn.Kathir.Vol.6.pdf",
            surahRange = "Yusuf (12) – Al-Isra (17)"
        ),
        VolumeInfo(
            volume = 7,
            title = "Volume 7",
            archiveId = "TafsirIbnKathirVol7",
            fileName = "Tafsir.Ibn.Kathir.Vol.7.pdf",
            surahRange = "Al-Kahf (18) – Al-Mu'minun (23)"
        ),
        VolumeInfo(
            volume = 8,
            title = "Volume 8",
            archiveId = "TafsirIbnKathirVol8",
            fileName = "Tafsir.Ibn.Kathir.Vol.8.pdf",
            surahRange = "An-Nur (24) – Al-Ahzab (33)"
        ),
        VolumeInfo(
            volume = 9,
            title = "Volume 9",
            archiveId = "TafsirIbnKathirVol9",
            fileName = "Tafsir.Ibn.Kathir.Vol.9.pdf",
            surahRange = "Saba (34) – Az-Zumar (39)"
        ),
        VolumeInfo(
            volume = 10,
            title = "Volume 10",
            archiveId = "TafsirIbnKathirVol10",
            fileName = "Tafsir.Ibn.Kathir.Vol.10.pdf",
            surahRange = "Ghafir (40) – An-Nas (114)"
        )
    )

    fun forVolume(volume: Int): VolumeInfo? = all.find { it.volume == volume }
}
