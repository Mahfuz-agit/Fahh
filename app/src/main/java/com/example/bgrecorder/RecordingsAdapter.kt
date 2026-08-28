package com.example.bgrecorder

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordingsAdapter(private var items: List<RecordingItem>) :
    RecyclerView.Adapter<RecordingsAdapter.VH>() {

    private var player: MediaPlayer? = null

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(android.R.id.text1)
        val meta: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.meta.text = "${item.date}  •  ${item.sizeKb} KB"
        holder.itemView.setOnClickListener {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(item.file.absolutePath)
                prepare()
                start()
            }
        }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<RecordingItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
