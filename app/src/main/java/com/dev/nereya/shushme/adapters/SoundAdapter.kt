package com.dev.nereya.shushme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.nereya.shushme.R
import com.dev.nereya.shushme.databinding.SoundItemBinding
import com.dev.nereya.shushme.interfaces.SoundSelectCallback
import com.dev.nereya.shushme.model.SoundItem
import androidx.core.graphics.toColorInt
import com.dev.nereya.shushme.model.DataManager

class SoundAdapter(
    private val soundList: List<SoundItem>,
    private val isSelectionMode: Boolean
) : RecyclerView.Adapter<SoundAdapter.SoundViewHolder>() {

    var soundCallback: SoundSelectCallback? = null
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val binding = SoundItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val item = soundList[position]
        holder.binding.soundLBLTitle.text = item.title
        holder.binding.soundLBLAuthor.text = item.author

        if (isSelectionMode) {
            val isCurrentlySelected = item.path == DataManager.currentSound?.path

            if (isCurrentlySelected) {
                holder.binding.soundCVData.strokeColor = "#647de6".toColorInt()
                holder.binding.soundImgAction.setImageResource(R.drawable.selected_sound)
            } else {
                holder.binding.soundCVData.strokeColor = "#e1e6f0".toColorInt()
                holder.binding.soundImgAction.setImageResource(R.drawable.unselected_sound)
            }
        } else {
            holder.binding.soundImgAction.setImageResource(R.drawable.download_icon)
        }

        holder.binding.soundImgAction.setOnClickListener {
            if (isSelectionMode) {
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    soundList[selectedPosition].isChosen = false
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = holder.absoluteAdapterPosition
                soundList[selectedPosition].isChosen = true
                notifyItemChanged(selectedPosition)

                soundCallback?.onSoundSelected(item, selectedPosition)
            } else {
                holder.binding.soundImgAction.visibility = View.GONE
                soundCallback?.onSoundSelected(item, holder.absoluteAdapterPosition)
            }
        }

        holder.binding.root.setOnClickListener {
            soundCallback?.onSoundSelected(
                item,
                holder.absoluteAdapterPosition
            )
        }
    }

    override fun getItemCount(): Int = soundList.size

    inner class SoundViewHolder(val binding: SoundItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}