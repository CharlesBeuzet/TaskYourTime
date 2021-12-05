package com.example.taskyourtime.todolist

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.TodolistItemCellBinding
import com.example.taskyourtime.model.ToDoItem
import com.example.taskyourtime.services.ToDoItemService


private lateinit var binding: TodolistItemCellBinding

class ListItemAdapter(
        private val data: MutableList<ToDoItem>,
        private val itemService: ToDoItemService,
        private val context: Context?,
) : RecyclerView.Adapter<ListItemAdapter.ListItemHolder>() {

    class ListItemHolder(binding: TodolistItemCellBinding) : RecyclerView.ViewHolder(binding.root) {
        val contentItem = binding.ItemList
        val layout = binding.root
        val checkbox = binding.checkbox
        val deletebutton = binding.deleteButton
        val sidebarView = binding.sidebarView
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ListItemAdapter.ListItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        com.example.taskyourtime.todolist.binding = TodolistItemCellBinding.inflate(inflater, parent, false)
        return ListItemAdapter.ListItemHolder(com.example.taskyourtime.todolist.binding)
    }

    override fun onBindViewHolder(holder: ListItemAdapter.ListItemHolder, position: Int) {
        val data: ToDoItem = data[position]
        holder.contentItem.text = data.content
        val id_item = data.id.toString()
        if(data.done!!){
            holder.checkbox.isChecked = true
            holder.contentItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            if (context != null) {
                holder.sidebarView.background = ColorDrawable(context.getColor(R.color.colorPrimaryDark))
            }
        }
        holder.checkbox.setOnClickListener{
            itemService.updateItem(id_item,holder.checkbox.isChecked)
            if(holder.checkbox.isChecked){
                holder.contentItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                if (context != null) {
                    holder.sidebarView.background = ColorDrawable(context.getColor(R.color.colorPrimaryDark))
                }
            }
            else{
                holder.contentItem.paintFlags = 0
                if (context != null) {
                    holder.sidebarView.background = ColorDrawable(context.getColor(R.color.orange))
                }
            }
        }
        holder.deletebutton.setOnClickListener{
            itemService.deleteNote(id_item)
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }
}
