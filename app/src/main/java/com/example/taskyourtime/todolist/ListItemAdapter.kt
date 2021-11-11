package com.example.taskyourtime.todolist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.taskyourtime.databinding.TodolistItemCellBinding
import com.example.taskyourtime.model.ToDoItem
import com.example.taskyourtime.services.ToDoItemService


private lateinit var binding: TodolistItemCellBinding

class ListItemAdapter(
        private val data: MutableList<ToDoItem>,
        private val itemService: ToDoItemService,
) : RecyclerView.Adapter<ListItemAdapter.ListItemHolder>() {

    class ListItemHolder(binding: TodolistItemCellBinding) : RecyclerView.ViewHolder(binding.root) {
        val contentItem = binding.ItemList
        val layout = binding.root
        val checkbox = binding.checkbox
        val deletebutton = binding.deleteButton
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
        holder.checkbox.isChecked = data.done!!
        holder.checkbox.setOnClickListener{
            itemService.updateItem(id_item,holder.checkbox.isChecked)
        }
        holder.deletebutton.setOnClickListener{
            itemService.deleteNote(id_item)
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }
}
