package com.tafsir.ibnkathir.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tafsir.ibnkathir.data.Surah
import com.tafsir.ibnkathir.databinding.ItemSurahBinding

class SurahAdapter(
    private val onSurahClick: (Surah) -> Unit
) : ListAdapter<Surah, SurahAdapter.SurahViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val binding = ItemSurahBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SurahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SurahViewHolder(
        private val binding: ItemSurahBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(surah: Surah) {
            binding.tvSurahNumber.text  = surah.number.toString()
            binding.tvNameEnglish.text  = surah.nameEnglish
            binding.tvNameArabic.text   = surah.nameArabic
            binding.tvMeaning.text      = surah.nameMeaning
            binding.tvVolume.text       = "Vol. ${surah.volume}"
            binding.root.setOnClickListener { onSurahClick(surah) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Surah>() {
            override fun areItemsTheSame(old: Surah, new: Surah) = old.number == new.number
            override fun areContentsTheSame(old: Surah, new: Surah) = old == new
        }
    }
}
