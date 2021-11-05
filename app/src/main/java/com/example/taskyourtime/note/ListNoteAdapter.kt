package com.example.taskyourtime.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.databinding.NoteItemCellBinding
import com.example.taskyourtime.model.Note

private lateinit var binding: NoteItemCellBinding

class ListNoteAdapter(
    private val data: MutableList<Note>,
) : RecyclerView.Adapter<ListNoteAdapter.ListNoteHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListNoteHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = NoteItemCellBinding.inflate(inflater, parent, false)
        return ListNoteHolder(binding)
    }

    override fun onBindViewHolder(holder: ListNoteHolder, position: Int) {
        val data: Note = data[position]
        holder.nameNote.text = data.name
        holder.contentNote.text = data.content
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ListNoteHolder(binding: NoteItemCellBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameNote = binding.nameNote
        val contentNote = binding.contentNote
        val layout = binding.root
    }
}
