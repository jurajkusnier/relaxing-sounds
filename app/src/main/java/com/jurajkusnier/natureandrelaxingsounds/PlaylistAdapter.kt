package com.jurajkusnier.natureandrelaxingsounds

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

data class Sound(
    val id: String,
    val title: String,
    @DrawableRes val icon: Int?,
    val isSelected: Boolean
)

class PlaylistAdapter : ListAdapter<Sound, PlaylistAdapter.ViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(sound: Sound) {
            view.findViewById<TextView>(R.id.itemTitle).text = "$sound"
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