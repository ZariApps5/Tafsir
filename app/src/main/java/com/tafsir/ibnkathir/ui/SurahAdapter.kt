package com.tafsir.ibnkathir.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tafsir.ibnkathir.data.Surah
import com.tafsir.ibnkathir.data.Volumes
import com.tafsir.ibnkathir.databinding.ItemSurahBinding
import com.tafsir.ibnkathir.databinding.ItemVolumeHeaderBinding

/**
 * Displays a flat, searchable list of Surahs.
 *
 * When displaying the full list (not search results), each volume boundary
 * is preceded by a header card showing the volume number and Surah range.
 *
 * Bookmarked surahs display a filled bookmark icon.
 */
class SurahAdapter(
    private val onSurahClick: (Surah) -> Unit
) : ListAdapter<SurahAdapter.ListItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    /** Union type for list rows. */
    sealed class ListItem {
        data class Header(val volume: Int, val range: String) : ListItem()
        data class SurahItem(val surah: Surah, val isBookmarked: Boolean) : ListItem()
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /** Submit search results without volume headers. */
    fun submitSurahs(surahs: List<Surah>, bookmarked: Set<Int>) {
        submitList(surahs.map { ListItem.SurahItem(it, it.number in bookmarked) })
    }

    /** Submit the full list with volume section headers inserted. */
    fun submitSurahsWithHeaders(surahs: List<Surah>, bookmarked: Set<Int>) {
        val items = mutableListOf<ListItem>()
        var lastVolume = -1
        for (surah in surahs) {
            if (surah.volume != lastVolume) {
                val vol = Volumes.forVolume(surah.volume)
                items += ListItem.Header(
                    volume = surah.volume,
                    range  = vol?.surahRange ?: "Volume ${surah.volume}"
                )
                lastVolume = surah.volume
            }
            items += ListItem.SurahItem(surah, surah.number in bookmarked)
        }
        submitList(items)
    }

    // ── ViewHolder wiring ──────────────────────────────────────────────────────

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ListItem.Header    -> TYPE_HEADER
        is ListItem.SurahItem -> TYPE_SURAH
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                ItemVolumeHeaderBinding.inflate(inflater, parent, false)
            )
            else -> SurahViewHolder(
                ItemSurahBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItem.Header    -> (holder as HeaderViewHolder).bind(item)
            is ListItem.SurahItem -> (holder as SurahViewHolder).bind(item)
        }
    }

    // ── ViewHolders ────────────────────────────────────────────────────────────

    inner class HeaderViewHolder(
        private val binding: ItemVolumeHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.Header) {
            binding.tvVolumeTitle.text = "Volume ${item.volume}"
            binding.tvVolumeRange.text = item.range
        }
    }

    inner class SurahViewHolder(
        private val binding: ItemSurahBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.SurahItem) {
            val surah = item.surah
            binding.tvSurahNumber.text = surah.number.toString()
            binding.tvNameEnglish.text = surah.nameEnglish
            binding.tvNameArabic.text  = surah.nameArabic
            binding.tvMeaning.text     = surah.nameMeaning
            binding.tvVolume.text      = "Vol. ${surah.volume}"
            binding.ivBookmark.visibility = if (item.isBookmarked) View.VISIBLE else View.GONE
            binding.root.setOnClickListener { onSurahClick(surah) }
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SURAH  = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(old: ListItem, new: ListItem) = when {
                old is ListItem.Header    && new is ListItem.Header    -> old.volume == new.volume
                old is ListItem.SurahItem && new is ListItem.SurahItem -> old.surah.number == new.surah.number
                else -> false
            }
            override fun areContentsTheSame(old: ListItem, new: ListItem) = old == new
        }
    }
}
