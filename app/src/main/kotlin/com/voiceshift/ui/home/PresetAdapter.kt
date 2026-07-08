package com.voiceshift.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.voiceshift.R
import com.voiceshift.model.VoicePreset

class PresetAdapter(
    private val onPresetClick: (VoicePreset) -> Unit
) : ListAdapter<VoicePreset, PresetAdapter.PresetViewHolder>(PresetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_preset, parent, false)
        return PresetViewHolder(view, onPresetClick)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PresetViewHolder(
        itemView: View,
        private val onPresetClick: (VoicePreset) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvPresetName)
        private val lottiePlay: LottieAnimationView = itemView.findViewById(R.id.lottiePlay)

        fun bind(preset: VoicePreset) {
            tvName.text = preset.name
            itemView.setOnClickListener { onPresetClick(preset) }
        }
    }
}

class PresetDiffCallback : DiffUtil.ItemCallback<VoicePreset>() {
    override fun areItemsTheSame(oldItem: VoicePreset, newItem: VoicePreset): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: VoicePreset, newItem: VoicePreset): Boolean {
        return oldItem == newItem
    }
}
