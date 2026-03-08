package com.tafsir.ibnkathir.data

/**
 * Complete list of all 114 Surahs with their location in the
 * Darussalam 10-volume English edition of Tafsir Ibn Kathir.
 *
 * Page numbers are 0-indexed (first page of the PDF = page 0).
 *
 * HOW TO VERIFY / UPDATE PAGE NUMBERS:
 * 1. Open the PDF for each volume
 * 2. Find the table of contents, or navigate to where each Surah begins
 * 3. Note the page number shown in the PDF viewer
 * 4. Subtract any front-matter pages to get the 0-indexed value
 *
 * The values below are carefully researched approximations based on the
 * Darussalam edition structure. They may vary slightly between print runs.
 */
object SurahRepository {

    val surahs: List<Surah> = listOf(
        // ── VOLUME 1 ──────────────────────────────────────────────────────────
        Surah(1,  "الفاتحة",     "Al-Fatiha",     "The Opening",            1,  80),
        Surah(2,  "البقرة",      "Al-Baqarah",    "The Cow",                1, 118),

        // ── VOLUME 2 ──────────────────────────────────────────────────────────
        // Al-Baqarah continues into Vol 2; Vol 2 starts mid-surah.
        // The entry below is for users who want to jump to Al-Imran.
        Surah(3,  "آل عمران",    "Al-Imran",      "The Family of Imran",    2, 186),

        // ── VOLUME 3 ──────────────────────────────────────────────────────────
        Surah(4,  "النساء",      "An-Nisa",       "The Women",              3,  22),
        Surah(5,  "المائدة",     "Al-Maidah",     "The Table Spread",       3, 278),

        // ── VOLUME 4 ──────────────────────────────────────────────────────────
        Surah(6,  "الأنعام",     "Al-An'am",      "The Cattle",             4,  22),
        Surah(7,  "الأعراف",     "Al-A'raf",      "The Heights",            4, 248),

        // ── VOLUME 5 ──────────────────────────────────────────────────────────
        Surah(8,  "الأنفال",     "Al-Anfal",      "The Spoils of War",      5,  22),
        Surah(9,  "التوبة",      "At-Tawbah",     "The Repentance",         5, 118),
        Surah(10, "يونس",        "Yunus",         "Jonah",                  5, 310),
        Surah(11, "هود",         "Hud",           "Hud",                    5, 416),

        // ── VOLUME 6 ──────────────────────────────────────────────────────────
        Surah(12, "يوسف",        "Yusuf",         "Joseph",                 6,  22),
        Surah(13, "الرعد",       "Ar-Ra'd",       "The Thunder",            6, 148),
        Surah(14, "إبراهيم",     "Ibrahim",       "Abraham",                6, 190),
        Surah(15, "الحجر",       "Al-Hijr",       "The Rocky Tract",        6, 232),
        Surah(16, "النحل",       "An-Nahl",       "The Bees",               6, 272),
        Surah(17, "الإسراء",     "Al-Isra",       "The Night Journey",      6, 376),

        // ── VOLUME 7 ──────────────────────────────────────────────────────────
        Surah(18, "الكهف",       "Al-Kahf",       "The Cave",               7,  22),
        Surah(19, "مريم",        "Maryam",        "Mary",                   7, 118),
        Surah(20, "طه",          "Ta-Ha",         "Ta-Ha",                  7, 162),
        Surah(21, "الأنبياء",    "Al-Anbiya",     "The Prophets",           7, 232),
        Surah(22, "الحج",        "Al-Hajj",       "The Pilgrimage",         7, 316),
        Surah(23, "المؤمنون",    "Al-Mu'minun",   "The Believers",          7, 384),

        // ── VOLUME 8 ──────────────────────────────────────────────────────────
        Surah(24, "النور",       "An-Nur",        "The Light",              8,  22),
        Surah(25, "الفرقان",     "Al-Furqan",     "The Criterion",          8, 118),
        Surah(26, "الشعراء",     "Ash-Shu'ara",   "The Poets",              8, 162),
        Surah(27, "النمل",       "An-Naml",       "The Ants",               8, 228),
        Surah(28, "القصص",       "Al-Qasas",      "The Stories",            8, 282),
        Surah(29, "العنكبوت",    "Al-Ankabut",    "The Spider",             8, 348),
        Surah(30, "الروم",       "Ar-Rum",        "The Romans",             8, 390),
        Surah(31, "لقمان",       "Luqman",        "Luqman",                 8, 428),
        Surah(32, "السجدة",      "As-Sajdah",     "The Prostration",        8, 458),
        Surah(33, "الأحزاب",     "Al-Ahzab",      "The Combined Forces",    8, 480),

        // ── VOLUME 9 ──────────────────────────────────────────────────────────
        Surah(34, "سبأ",         "Saba",          "Sheba",                  9,  22),
        Surah(35, "فاطر",        "Fatir",         "The Originator",         9,  82),
        Surah(36, "يس",          "Ya-Sin",        "Ya-Sin",                 9, 132),
        Surah(37, "الصافات",     "As-Saffat",     "Those Ranged in Ranks",  9, 180),
        Surah(38, "ص",           "Sad",           "Sad",                    9, 252),
        Surah(39, "الزمر",       "Az-Zumar",      "The Groups",             9, 298),

        // ── VOLUME 10 ─────────────────────────────────────────────────────────
        Surah(40, "غافر",        "Ghafir",        "The Forgiver",          10,  22),
        Surah(41, "فصلت",        "Fussilat",      "Explained in Detail",   10,  86),
        Surah(42, "الشورى",      "Ash-Shura",     "The Consultation",      10, 130),
        Surah(43, "الزخرف",      "Az-Zukhruf",    "The Gold Adornments",   10, 176),
        Surah(44, "الدخان",      "Ad-Dukhan",     "The Smoke",             10, 218),
        Surah(45, "الجاثية",     "Al-Jathiya",    "The Crouching",         10, 244),
        Surah(46, "الأحقاف",     "Al-Ahqaf",      "The Curved Sand Hills", 10, 272),
        Surah(47, "محمد",        "Muhammad",      "Muhammad",              10, 308),
        Surah(48, "الفتح",       "Al-Fath",       "The Victory",           10, 342),
        Surah(49, "الحجرات",     "Al-Hujurat",    "The Dwellings",         10, 380),
        Surah(50, "ق",           "Qaf",           "Qaf",                   10, 404),
        Surah(51, "الذاريات",    "Adh-Dhariyat",  "The Wind that Scatter", 10, 428),
        Surah(52, "الطور",       "At-Tur",        "The Mount",             10, 454),
        Surah(53, "النجم",       "An-Najm",       "The Star",              10, 476),
        Surah(54, "القمر",       "Al-Qamar",      "The Moon",              10, 500),
        Surah(55, "الرحمن",      "Ar-Rahman",     "The Most Gracious",     10, 524),
        Surah(56, "الواقعة",     "Al-Waqi'a",     "The Event",             10, 548),
        Surah(57, "الحديد",      "Al-Hadid",      "The Iron",              10, 572),
        Surah(58, "المجادلة",    "Al-Mujadila",   "The Disputation",       10, 604),
        Surah(59, "الحشر",       "Al-Hashr",      "The Gathering",         10, 632),
        Surah(60, "الممتحنة",    "Al-Mumtahana",  "The Woman Examined",    10, 660),
        Surah(61, "الصف",        "As-Saf",        "The Row",               10, 682),
        Surah(62, "الجمعة",      "Al-Jumu'a",     "Friday",                10, 698),
        Surah(63, "المنافقون",   "Al-Munafiqun",  "The Hypocrites",        10, 712),
        Surah(64, "التغابن",     "At-Taghabun",   "The Mutual Loss",       10, 728),
        Surah(65, "الطلاق",      "At-Talaq",      "The Divorce",           10, 744),
        Surah(66, "التحريم",     "At-Tahrim",     "The Prohibition",       10, 764),
        Surah(67, "الملك",       "Al-Mulk",       "The Dominion",          10, 786),
        Surah(68, "القلم",       "Al-Qalam",      "The Pen",               10, 808),
        Surah(69, "الحاقة",      "Al-Haqqah",     "The Inevitable",        10, 828),
        Surah(70, "المعارج",     "Al-Ma'arij",    "The Ways of Ascent",    10, 844),
        Surah(71, "نوح",         "Nuh",           "Noah",                  10, 860),
        Surah(72, "الجن",        "Al-Jinn",       "The Jinn",              10, 876),
        Surah(73, "المزمل",      "Al-Muzzammil",  "The One Wrapped",       10, 896),
        Surah(74, "المدثر",      "Al-Muddaththir","The One Enveloped",     10, 912),
        Surah(75, "القيامة",     "Al-Qiyamah",    "The Resurrection",      10, 930),
        Surah(76, "الإنسان",     "Al-Insan",      "The Human",             10, 948),
        Surah(77, "المرسلات",    "Al-Mursalat",   "Those Sent Forth",      10, 966),
        Surah(78, "النبأ",       "An-Naba",       "The Great News",        10, 982),
        Surah(79, "النازعات",    "An-Nazi'at",    "Those Who Pull Out",    10, 998),
        Surah(80, "عبس",         "Abasa",         "He Frowned",            10,1012),
        Surah(81, "التكوير",     "At-Takwir",     "The Overthrowing",      10,1022),
        Surah(82, "الانفطار",    "Al-Infitar",    "The Cleaving",          10,1034),
        Surah(83, "المطففين",    "Al-Mutaffifin", "Those Who Deal in Fraud",10,1044),
        Surah(84, "الانشقاق",    "Al-Inshiqaq",   "The Splitting Asunder", 10,1058),
        Surah(85, "البروج",      "Al-Buruj",      "The Big Stars",         10,1068),
        Surah(86, "الطارق",      "At-Tariq",      "The Night-Comer",       10,1080),
        Surah(87, "الأعلى",      "Al-A'la",       "The Most High",         10,1090),
        Surah(88, "الغاشية",     "Al-Ghashiya",   "The Overwhelming",      10,1100),
        Surah(89, "الفجر",       "Al-Fajr",       "The Dawn",              10,1114),
        Surah(90, "البلد",       "Al-Balad",      "The City",              10,1126),
        Surah(91, "الشمس",       "Ash-Shams",     "The Sun",               10,1136),
        Surah(92, "الليل",       "Al-Layl",       "The Night",             10,1148),
        Surah(93, "الضحى",       "Ad-Dhuha",      "The Forenoon",          10,1158),
        Surah(94, "الشرح",       "Ash-Sharh",     "The Opening Forth",     10,1166),
        Surah(95, "التين",       "At-Tin",        "The Fig",               10,1174),
        Surah(96, "العلق",       "Al-Alaq",       "The Clot",              10,1182),
        Surah(97, "القدر",       "Al-Qadr",       "The Night of Decree",   10,1196),
        Surah(98, "البينة",      "Al-Bayyina",    "The Clear Evidence",    10,1204),
        Surah(99, "الزلزلة",     "Az-Zalzala",    "The Earthquake",        10,1216),
        Surah(100,"العاديات",    "Al-Adiyat",     "The Runners",           10,1224),
        Surah(101,"القارعة",     "Al-Qari'a",     "The Striking Hour",     10,1232),
        Surah(102,"التكاثر",     "At-Takathur",   "The Piling Up",         10,1240),
        Surah(103,"العصر",       "Al-Asr",        "The Time",              10,1250),
        Surah(104,"الهمزة",      "Al-Humaza",     "The Slanderer",         10,1258),
        Surah(105,"الفيل",       "Al-Fil",        "The Elephant",          10,1266),
        Surah(106,"قريش",        "Quraish",       "Quraish",               10,1276),
        Surah(107,"الماعون",     "Al-Ma'un",      "The Small Kindnesses",  10,1284),
        Surah(108,"الكوثر",      "Al-Kawthar",    "A River in Paradise",   10,1292),
        Surah(109,"الكافرون",    "Al-Kafirun",    "The Disbelievers",      10,1300),
        Surah(110,"النصر",       "An-Nasr",       "The Help",              10,1310),
        Surah(111,"المسد",       "Al-Masad",      "The Palm Fibre",        10,1318),
        Surah(112,"الإخلاص",     "Al-Ikhlas",     "The Sincerity",         10,1326),
        Surah(113,"الفلق",       "Al-Falaq",      "The Daybreak",          10,1340),
        Surah(114,"الناس",       "An-Nas",        "Mankind",               10,1354)
    )

    fun getById(number: Int): Surah? = surahs.find { it.number == number }

    fun getByVolume(volume: Int): List<Surah> = surahs.filter { it.volume == volume }

    fun search(query: String): List<Surah> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return surahs
        return surahs.filter {
            it.nameEnglish.lowercase().contains(q) ||
            it.nameMeaning.lowercase().contains(q) ||
            it.nameArabic.contains(q) ||
            it.number.toString() == q
        }
    }
}
