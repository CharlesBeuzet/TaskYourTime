package com.example.taskyourtime.note

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.databinding.NoteItemCellBinding
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.services.NoteService


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
        holder.nameNote.text = Html.fromHtml(data.name,Html.FROM_HTML_MODE_LEGACY)
        holder.contentNote.text = Html.fromHtml(data.content,Html.FROM_HTML_MODE_LEGACY)
        val idNote = data.id.toString()
        holder.deleteNoteButton.setOnClickListener{
            Log.d("Asupprimer","Note dont l'id est $idNote a été supprimée")
            noteService.deleteNote(idNote)
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
