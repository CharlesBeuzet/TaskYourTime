package com.example.taskyourtime.group

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.databinding.GroupItemCellBinding
import com.example.taskyourtime.model.Group
import com.example.taskyourtime.services.GroupService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.taskyourtime.model.User
import com.example.taskyourtime.services.UserService
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database


private lateinit var binding: GroupItemCellBinding


class ListGroupAdapter(
    private val data: MutableList<Group>,
    private val groupService: GroupService,
    private val userService: UserService,
    private val listener: OnItemClickListener,
): RecyclerView.Adapter<ListGroupAdapter.ListGroupHolder>() {

    private val database : DatabaseReference = Firebase.database.reference

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
        var ownerUser: Unit = userService.getUserById(data.ownerId.toString()).observeForever{
                success ->
            if(success != null){
                holder.groupOwner.text = Html.fromHtml(success.firstName + " " + success.lastName,Html.FROM_HTML_MODE_LEGACY)
            }
        }
        val idGroup = data.id.toString()
        /*holder.deleteGroupButton.setOnClickListener{
            Log.d("Asupprimer","Groupe dont l'id est id a été supprimée")
            groupService.deleteGroup(idGroup)
        }*/

        if(data.userIdList[Firebase.auth.uid] == "no"){
            holder.buttonAccept.visibility = View.VISIBLE
            holder.buttonRefuse.visibility = View.VISIBLE

            holder.buttonAccept.setOnClickListener{
                database.child("groups").child(data.id.toString()).child("userIdList").child(Firebase.auth.uid.toString()).setValue("yes")
                holder.buttonAccept.visibility = View.GONE
                holder.buttonRefuse.visibility = View.GONE
            }

            holder.buttonRefuse.setOnClickListener{
                database.child("groups").child(data.id.toString()).child("userIdList").child(Firebase.auth.uid.toString()).removeValue()
                holder.buttonAccept.visibility = View.GONE
                holder.buttonRefuse.visibility = View.GONE
            }

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ListGroupHolder(binding: GroupItemCellBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        val groupName = binding.groupName
        val groupOwner = binding.groupOwner
        val buttonAccept = binding.buttonAccept
        val buttonRefuse = binding.buttonRefuse
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