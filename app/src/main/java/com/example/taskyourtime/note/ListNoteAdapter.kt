package com.example.taskyourtime.note

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.databinding.NoteItemCellBinding
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.services.NoteService
import java.nio.file.Files.delete


private lateinit var binding: NoteItemCellBinding


class ListNoteAdapter(
    private val data: MutableList<Note>,
    private val noteService: NoteService,
    private val listener: OnItemClickListener,
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
        val id_note = data.id.toString()
        holder.deleteNoteButton.setOnClickListener{
            Log.d("Asupprimer","Note dont l'id est $id_note a été supprimée")
            noteService.deleteNote(id_note)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ListNoteHolder(binding: NoteItemCellBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        val nameNote = binding.nameNote
        val contentNote = binding.contentNote
        val deleteNoteButton = binding.deleteNoteButton
        val layout = binding.root

        init{
            binding.root.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION)
            {
                listener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}
