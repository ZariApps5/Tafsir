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
    val surahRange: String,       // Human-readable, e.g. "Al-Fatiha – Al-Baqarah"
    val estimatedSizeMb: Int = 40 // Approximate download size shown before download
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
        VolumeInfo(1,  "Volume 1",  "TafsirIbnKathirVol1",  "Tafsir.Ibn.Kathir.Vol.1.pdf",  "Al-Fatiha (1) – Al-Baqarah (2)",        estimatedSizeMb = 38),
        VolumeInfo(2,  "Volume 2",  "TafsirIbnKathirVol2",  "Tafsir.Ibn.Kathir.Vol.2.pdf",  "Al-Baqarah cont. – Al-Imran (3)",       estimatedSizeMb = 42),
        VolumeInfo(3,  "Volume 3",  "TafsirIbnKathirVol3",  "Tafsir.Ibn.Kathir.Vol.3.pdf",  "An-Nisa (4) – Al-Maidah (5)",           estimatedSizeMb = 40),
        VolumeInfo(4,  "Volume 4",  "TafsirIbnKathirVol4",  "Tafsir.Ibn.Kathir.Vol.4.pdf",  "Al-An'am (6) – Al-A'raf (7)",           estimatedSizeMb = 41),
        VolumeInfo(5,  "Volume 5",  "TafsirIbnKathirVol5",  "Tafsir.Ibn.Kathir.Vol.5.pdf",  "Al-Anfal (8) – Hud (11)",               estimatedSizeMb = 43),
        VolumeInfo(6,  "Volume 6",  "TafsirIbnKathirVol6",  "Tafsir.Ibn.Kathir.Vol.6.pdf",  "Yusuf (12) – Al-Isra (17)",             estimatedSizeMb = 39),
        VolumeInfo(7,  "Volume 7",  "TafsirIbnKathirVol7",  "Tafsir.Ibn.Kathir.Vol.7.pdf",  "Al-Kahf (18) – Al-Mu'minun (23)",       estimatedSizeMb = 37),
        VolumeInfo(8,  "Volume 8",  "TafsirIbnKathirVol8",  "Tafsir.Ibn.Kathir.Vol.8.pdf",  "An-Nur (24) – Al-Ahzab (33)",           estimatedSizeMb = 44),
        VolumeInfo(9,  "Volume 9",  "TafsirIbnKathirVol9",  "Tafsir.Ibn.Kathir.Vol.9.pdf",  "Saba (34) – Az-Zumar (39)",             estimatedSizeMb = 36),
        VolumeInfo(10, "Volume 10", "TafsirIbnKathirVol10", "Tafsir.Ibn.Kathir.Vol.10.pdf", "Ghafir (40) – An-Nas (114)",            estimatedSizeMb = 48)
    )

    fun forVolume(volume: Int): VolumeInfo? = all.find { it.volume == volume }
}
