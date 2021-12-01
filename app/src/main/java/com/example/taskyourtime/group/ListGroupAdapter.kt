package com.example.taskyourtime.group

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.databinding.GroupItemCellBinding
import com.example.taskyourtime.model.Group
import com.example.taskyourtime.note.ListNoteAdapter
import com.example.taskyourtime.services.GroupService

private lateinit var binding: GroupItemCellBinding


class ListGroupAdapter(
    private val data: MutableList<Group>,
    private val noteService: GroupService,
    private val listener: ListGroupActivity,
): RecyclerView.Adapter<ListGroupAdapter.ListGroupHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListGroupHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = GroupItemCellBinding.inflate(inflater, parent, false)
        return ListGroupHolder(binding)
    }

    override fun onBindViewHolder(holder: ListGroupHolder, position: Int) {
        val data: Group = data[position]
        holder.groupName.text = Html.fromHtml(data.name, Html.FROM_HTML_MODE_LEGACY)
        holder.groupOwner.text = Html.fromHtml(data.ownerId,Html.FROM_HTML_MODE_LEGACY)
        val idGroup = data.id.toString()
        /*holder.deleteGroupButton.setOnClickListener{
            Log.d("Asupprimer","Groupe dont l'id est id a été supprimée")
            groupService.deleteGroup(idGroup)
        }*/
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ListGroupHolder(binding: GroupItemCellBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        val groupName = binding.groupName
        val groupOwner = binding.groupOwner
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