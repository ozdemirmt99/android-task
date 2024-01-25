package com.example.vero

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerItems(private val items: List<com.example.vero.Models.Item>) :
    RecyclerView.Adapter<RecyclerItems.Item>() {
    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleText)
        val task: TextView = itemView.findViewById(R.id.taskText)
        val description: TextView = itemView.findViewById(R.id.desriptionText)
        val colorCode: TextView = itemView.findViewById(R.id.colorCodeText)


//
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_of_recycler, parent, false)
        return Item(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Item, position: Int) {

        holder.title.text = items[position].title
        holder.task.text = items[position].task
        holder.description.text = items[position].description
        holder.colorCode.text = items[position].colorCode
    }

}