# CLAUDE.md — Tafsir Ibn Kathir Android App

This file provides guidance for AI assistants working in this codebase.

---

## Project Overview

**Tafsir Ibn Kathir** is a native Android application for reading the classic Islamic exegesis (Tafsir) by Ibn Kathir. The text is distributed across 10 PDF volumes sourced from Archive.org (public domain). Users can browse all 114 Surahs, download volumes on-demand, read PDFs in-app, and persist bookmarks and reading positions.

- **Platform:** Android (min SDK 24 / Android 7.0, target SDK 34 / Android 14)
- **Language:** Kotlin (100%)
- **Application ID:** `com.tafsir.ibnkathir`
- **Build System:** Gradle with Android Gradle Plugin 9.1.0

---

## Repository Structure

```
Tafsir/
├── app/
│   ├── build.gradle                        # App-level build config
│   └── src/main/
│       ├── AndroidManifest.xml             # App manifest, permissions, activities
│       ├── java/com/tafsir/ibnkathir/
│       │   ├── MainActivity.kt             # Surah list screen (launch activity)
│       │   ├── TafsirReaderActivity.kt     # In-app PDF reader
│       │   ├── StorageManagerActivity.kt   # Volume download/delete manager
│       │   ├── data/
│       │   │   ├── Surah.kt               # Data class: one Surah entry
│       │   │   ├── SurahRepository.kt     # All 114 Surahs (hardcoded data)
│       │   │   └── VolumeInfo.kt          # 10 volume configs + Archive.org URLs
│       │   ├── ui/
│       │   │   ├── SurahAdapter.kt        # RecyclerView adapter for Surah list
│       │   │   └── VolumeDownloadAdapter.kt # Adapter for storage manager
│       │   └── util/
│       │       ├── PdfDownloadManager.kt  # HTTP download + file management
│       │       └── BookmarkManager.kt     # SharedPreferences persistence
│       └── res/
│           ├── layout/                    # 6 XML layout files
│           ├── menu/                      # 2 XML menu files
│           ├── drawable/                  # 8 vector drawable icons + bg
│           └── values/                    # colors.xml, strings.xml, themes.xml
├── build.gradle                           # Root Gradle config
├── settings.gradle                        # Plugin management + repositories
├── gradle.properties                      # Gradle JVM/AndroidX settings
└── gradlew / gradlew.bat                  # Gradle wrapper scripts
```

---

## Architecture & Design Patterns

The app uses a simple, flat Android architecture appropriate for its scope (~622 lines total):

| Pattern | Where Used |
|---|---|
| Repository | `SurahRepository` — single source of truth for Surah data |
| Adapter | `SurahAdapter`, `VolumeDownloadAdapter` — RecyclerView binding |
| Manager | `PdfDownloadManager`, `BookmarkManager` — encapsulated utilities |
| Sealed class | `DownloadResult` in `PdfDownloadManager` (Success/Error/AlreadyExists) |
| Data class | `Surah`, `VolumeInfo`, `VolumeRowState` |

**Data flow:**
```
SurahRepository (static)
    → MainActivity (search, bookmarks, volume headers)
        → TafsirReaderActivity (PDF load, page tracking)
        → StorageManagerActivity (download/delete volumes)

BookmarkManager (SharedPreferences)
    → read/write across MainActivity & TafsirReaderActivity

PdfDownloadManager (HTTP + filesystem)
    → called from StorageManagerActivity & checked in MainActivity
```

**Async:** Kotlin Coroutines (`Dispatchers.IO` for downloads/file I/O, `Dispatchers.Main` for UI updates). No third-party async libraries.

---

## Key Conventions

### Naming

| Kind | Convention | Example |
|---|---|---|
| Activities | `{Feature}Activity` | `TafsirReaderActivity` |
| Adapters | `{Entity}Adapter` | `SurahAdapter` |
| Managers/Utilities | `{Function}Manager` | `BookmarkManager` |
| Data classes | Noun | `Surah`, `VolumeInfo` |
| View IDs (XML) | `{type}_{name}` prefix | `tv_surah_name`, `iv_bookmark` |
| Layout files | `{scope}_{feature}.xml` | `item_surah.xml`, `activity_main.xml` |

### Code Style

- Kotlin official style (configured via `gradle.properties`: `kotlin.code.style=official`)
- **View Binding** is enabled — always use it instead of `findViewById`
- Coroutines via `lifecycleScope.launch` in activities; avoid `GlobalScope`
- Sealed classes for operation results (no bare booleans/nulls for multi-state outcomes)
- No dependency injection framework — pass dependencies explicitly or use companion objects

### Resource Conventions

- Colors defined centrally in `res/values/colors.xml` — never use hardcoded hex in layouts
- All user-visible strings in `res/values/strings.xml`
- Icons are vector drawables (XML), not raster images
- Consistent spacing: 8dp, 12dp, 14dp, 16dp increments

### Design Tokens

| Role | Color |
|---|---|
| Primary (Islamic green) | `#1B5E20` |
| Primary Dark | `#145214` |
| Primary Light | `#4C8C4A` |
| Accent (gold) | `#F9A825` |
| Text Primary | `#212121` |
| Text Secondary | `#757575` |
| Background | `#FAFAFA` |

---

## Build & Development

### Prerequisites

- Android Studio (Hedgehog or newer recommended)
- JDK 17+ (Gradle wrapper handles toolchain)
- Android SDK with API 24–34 installed

### Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean
./gradlew clean
```

Gradle JVM heap is set to 2 GB (`org.gradle.jvmargs=-Xmx2048m`).

### Installing on Device/Emulator

```bash
./gradlew installDebug
```

### Key Gradle Settings (gradle.properties)

```properties
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
```

---

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| `androidx.core:core-ktx` | 1.12.0 | Kotlin extensions for Android |
| `androidx.appcompat:appcompat` | 1.6.1 | Backwards-compatible Activity/UI |
| `com.google.android.material` | 1.11.0 | Material Design 3 components |
| `androidx.constraintlayout` | 2.1.4 | Complex layouts |
| `androidx.recyclerview` | 1.3.2 | Scrollable lists |
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | 2.7.0 | ViewModel + Coroutine scope |
| `androidx.lifecycle:lifecycle-livedata-ktx` | 2.7.0 | LiveData observers |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.7.0 | `lifecycleScope` |
| `com.github.barteksc:AndroidPdfViewer` | 3.2.0-beta.1 | In-app PDF rendering (JitPack) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | 1.7.3 | Coroutines support |

JitPack is required for `AndroidPdfViewer`. It is configured in `settings.gradle`.

---

## Data Sources

**Surah data** is fully hardcoded in `SurahRepository.kt`:
- 114 entries, each with: surah number, Arabic name, English name, English meaning, volume (1–10), and starting page number in that volume's PDF.
- **Do not replace this with a network fetch** — it is intentionally static and offline-capable.

**PDF files** are downloaded from Archive.org public domain URLs defined in `VolumeInfo.kt`:
- Format: `tafsir_ibn_kathir_vol_{1..10}.pdf`
- Stored locally at: `context.filesDir/volumes/tafsir_ibn_kathir_vol_{N}.pdf`
- Downloads use `.tmp` suffix and are atomically renamed on completion.

---

## Persistence

`BookmarkManager` wraps a single `SharedPreferences` file (`tafsir_reading_state`):

| Key pattern | Value | Meaning |
|---|---|---|
| `last_page_{surahNumber}` | Int | Last-read page for a Surah |
| `bookmark_{surahNumber}` | Boolean | Whether Surah is bookmarked |
| `night_mode` | Boolean | Global night mode state |

---

## Permissions

Declared in `AndroidManifest.xml`:

| Permission | Reason |
|---|---|
| `INTERNET` | Downloading PDF volumes |
| `READ_EXTERNAL_STORAGE` (maxSdk 32) | Legacy storage access |
| `WRITE_EXTERNAL_STORAGE` (maxSdk 29) | Legacy storage access |

On API 29+, PDFs are stored in the app's private files directory (`filesDir`) — no external storage permission needed.

HTTPS is enforced (`android:usesCleartextTraffic="false"`). Archive.org serves files over HTTPS.

---

## Testing

There are currently **no automated tests** in this repository. If adding tests:

- Unit tests go in `app/src/test/java/com/tafsir/ibnkathir/`
- Instrumented (Android) tests go in `app/src/androidTest/java/com/tafsir/ibnkathir/`
- Recommended frameworks: JUnit 4/5 for unit tests, Espresso for UI tests
- `BookmarkManager` and `PdfDownloadManager` are the highest-value units to test

---

## CI/CD

There is currently no CI/CD pipeline. If adding GitHub Actions:

- Use `actions/setup-java` with JDK 17
- Cache Gradle with `actions/cache` on `~/.gradle`
- Run `./gradlew assembleDebug` as the build step

---

## Activity Reference

### `MainActivity`
- **Purpose:** Entry point. Shows all 114 Surahs grouped by volume with search.
- **Key behavior:** Checks if the relevant volume PDF exists before opening the reader; redirects to `StorageManagerActivity` if not downloaded.
- **Search:** Filters across surah number, English name, Arabic name, and meaning. Case-insensitive.

### `TafsirReaderActivity`
- **Purpose:** Renders the selected Surah's volume PDF. Jumps to the Surah's start page.
- **Key behavior:** Auto-saves the last-viewed page on pause. Night mode applies a dark filter via `AndroidPdfViewer`.
- **Extras received:** `EXTRA_SURAH` (Surah object, Parcelable)

### `StorageManagerActivity`
- **Purpose:** Shows download status for all 10 volumes. Allows download and deletion.
- **Key behavior:** Tracks per-volume state (`NOT_DOWNLOADED`, `DOWNLOADING`, `DOWNLOADED`). Shows real-time progress via coroutine callbacks. Confirmation dialogs before destructive actions.

---

## Common Tasks

### Add a new feature screen
1. Create `{Feature}Activity.kt` in `com.tafsir.ibnkathir`
2. Declare it in `AndroidManifest.xml`
3. Create `activity_{feature}.xml` layout in `res/layout/`
4. Enable view binding: access via `ActivityFeatureBinding.inflate(layoutInflater)`

### Update Surah data
- Edit `SurahRepository.kt` — the `surahs` list and `getSurahsGroupedByVolume()` method
- Each `Surah` takes: `number`, `arabicName`, `englishName`, `meaning`, `volume`, `pageNumber`

### Add a new volume or change PDF URLs
- Edit `VolumeInfo.kt` — update the `volumes` list or individual `VolumeInfo` entries
- Fields: `volumeNumber`, `title`, `surahRange`, `downloadUrl`, `fileName`

### Change the app theme/colors
- Edit `res/values/colors.xml` for color tokens
- Edit `res/values/themes.xml` for theme attributes

---

## What NOT to Do

- **Do not** use `GlobalScope` for coroutines — always use `lifecycleScope` (Activity) or `viewModelScope` (ViewModel)
- **Do not** hardcode colors or dimensions inline in layouts — use resource references
- **Do not** use `findViewById` — View Binding is enabled and must be used
- **Do not** store PDFs in external storage (use `context.filesDir`)
- **Do not** add network calls to `SurahRepository` — Surah metadata is intentionally offline/static
- **Do not** skip confirmation dialogs for destructive actions (delete volume, etc.)
