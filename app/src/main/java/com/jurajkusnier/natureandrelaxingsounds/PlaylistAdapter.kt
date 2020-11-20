package com.jurajkusnier.natureandrelaxingsounds

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jurajkusnier.natureandrelaxingsounds.databinding.ListItemBinding

data class Sound(
    val id: String,
    val title: String,
    val iconUri: Uri?,
    val isSelected: Boolean
)

class PlaylistAdapter(private val onClickListener: (Sound) -> Unit) :
    ListAdapter<Sound, PlaylistAdapter.ViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ListItemBinding.inflate(inflater), onClickListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ListItemBinding, val onClickListener: (Sound) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sound: Sound) {
            binding.itemCardView.apply {
                isChecked = sound.isSelected
                setOnClickListener {
                    onClickListener(sound)
                }
            }
            binding.itemTitle.text = sound.title
            sound.iconUri?.let { binding.itemIcon.setImageURI(it) }
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Sound>() {
            override fun areItemsTheSame(old: Sound, new: Sound): Boolean {
                return old::class == new::class
            }

            override fun areContentsTheSame(old: Sound, new: Sound): Boolean {
                return old == new
            }
        }
    }

}