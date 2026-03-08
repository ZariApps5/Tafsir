package com.tafsir.ibnkathir.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tafsir.ibnkathir.databinding.ItemVolumeDownloadBinding

/**
 * Row states for a volume in the Storage Manager.
 *
 * The adapter is driven entirely by [VolumeRowState] — no separate
 * "downloading" flag in the Activity; just emit a new state.
 */
data class VolumeRowState(
    val volume: Int,
    val title: String,
    val surahRange: String,
    val status: Status,
    val downloadProgress: Int = 0,    // 0–100, only used when status == DOWNLOADING
    val actualSizeMb: Float = 0f,     // real size once downloaded
    val estimatedSizeMb: Int = 40     // shown before download
) {
    enum class Status { NOT_DOWNLOADED, DOWNLOADING, DOWNLOADED }

    val sizeLabel: String get() = when (status) {
        Status.DOWNLOADED    -> "%.1f MB".format(actualSizeMb)
        Status.DOWNLOADING   -> "$downloadProgress%"
        Status.NOT_DOWNLOADED -> "~$estimatedSizeMb MB"
    }
}

class VolumeDownloadAdapter(
    private val onDownload: (volume: Int) -> Unit,
    private val onDelete:   (volume: Int) -> Unit
) : ListAdapter<VolumeRowState, VolumeDownloadAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemVolumeDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(
        private val b: ItemVolumeDownloadBinding
    ) : RecyclerView.ViewHolder(b.root) {

        fun bind(row: VolumeRowState) {
            b.tvVolumeTitle.text  = row.title
            b.tvSurahRange.text   = row.surahRange
            b.tvSizeLabel.text    = row.sizeLabel

            when (row.status) {
                VolumeRowState.Status.NOT_DOWNLOADED -> {
                    b.progressBar.visibility  = View.GONE
                    b.btnAction.visibility    = View.VISIBLE
                    b.statusIcon.visibility   = View.GONE
                    b.btnAction.text          = "Download"
                    b.btnAction.isEnabled     = true
                    b.btnAction.setOnClickListener { onDownload(row.volume) }
                }

                VolumeRowState.Status.DOWNLOADING -> {
                    b.progressBar.visibility  = View.VISIBLE
                    b.progressBar.progress    = row.downloadProgress
                    b.btnAction.visibility    = View.GONE
                    b.statusIcon.visibility   = View.GONE
                }

                VolumeRowState.Status.DOWNLOADED -> {
                    b.progressBar.visibility  = View.GONE
                    b.btnAction.visibility    = View.VISIBLE
                    b.statusIcon.visibility   = View.VISIBLE
                    b.btnAction.text          = "Delete"
                    b.btnAction.isEnabled     = true
                    b.btnAction.setOnClickListener { onDelete(row.volume) }
                }
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<VolumeRowState>() {
            override fun areItemsTheSame(a: VolumeRowState, b: VolumeRowState) =
                a.volume == b.volume
            override fun areContentsTheSame(a: VolumeRowState, b: VolumeRowState) = a == b
        }
    }
}
