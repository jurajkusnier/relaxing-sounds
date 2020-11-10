package com.jurajkusnier.natureandrelaxingsounds

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

data class Sound(
    val id: String,
    val title: String,
    @DrawableRes val icon: Int?,
    val isSelected: Boolean
)

class PlaylistAdapter(private val onClickListener: (Sound) -> Unit) :
    ListAdapter<Sound, PlaylistAdapter.ViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    class ViewHolder(private val view: View, val onClickListener: (Sound) -> Unit) :
        RecyclerView.ViewHolder(view) {

        fun bind(sound: Sound) {
            view.findViewById<MaterialCardView>(R.id.itemCardView).apply {
                isChecked = sound.isSelected
                setOnClickListener {
                    onClickListener(sound)
                }
            }
            view.findViewById<TextView>(R.id.itemTitle).text = sound.title
            sound.icon?.let { view.findViewById<ImageView>(R.id.itemIcon).setImageResource(it) }
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