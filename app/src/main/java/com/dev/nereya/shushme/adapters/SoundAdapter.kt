package com.dev.nereya.shushme.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.nereya.shushme.databinding.SoundItemBinding
import com.dev.nereya.shushme.interfaces.SoundCallback
import com.dev.nereya.shushme.model.SoundItem

class SoundAdapter(private val soundList: List<SoundItem>) :
    RecyclerView.Adapter<SoundAdapter.SoundViewHolder>() {

    var soundCallback: SoundCallback? = null
    private var selectedPosition = RecyclerView.NO_POSITION


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SoundViewHolder {
        val binding = SoundItemBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val item = soundList[position]
        holder.binding.soundLBLTitle.text = item.title
        holder.binding.soundLBLAuthor.text = item.author
        holder.binding.soundLBLDuration.text = item.duration
        holder.binding.soundRadioButton.isChecked = item.isChosen
        holder.binding.soundRadioButton.setOnClickListener {
            if (selectedPosition != RecyclerView.NO_POSITION) {
                soundList[selectedPosition].isChosen = false
                notifyItemChanged(selectedPosition)
            }

            selectedPosition = holder.absoluteAdapterPosition
            soundList[selectedPosition].isChosen = true
            notifyItemChanged(selectedPosition)

            soundCallback?.onSoundSelected(item, selectedPosition)
        }
    }

    override fun getItemCount(): Int = soundList.size
    fun getItem(position: Int): SoundItem = soundList[position]

    inner class SoundViewHolder(val binding: SoundItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}