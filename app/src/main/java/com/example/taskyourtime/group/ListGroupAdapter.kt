package com.example.taskyourtime.group

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.GroupItemCellBinding
import com.example.taskyourtime.model.Group
import com.example.taskyourtime.services.GroupService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.taskyourtime.services.UserService
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database


private lateinit var binding: GroupItemCellBinding


class ListGroupAdapter(
    private val data: MutableList<Group>,
    private val groupService: GroupService,
    private val userService: UserService,
    private val listener: OnItemClickListener,
    private val context: Context?,
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
        val grp: Group = data[position]
        holder.groupName.text = Html.fromHtml(grp.name, Html.FROM_HTML_MODE_LEGACY)
        var ownerUser: Unit = userService.getUserById(grp.ownerId.toString()).observeForever{
                success ->
            if(success != null){
                holder.groupOwner.text = Html.fromHtml(success.firstName + " " + success.lastName,Html.FROM_HTML_MODE_LEGACY)
            }
        }
        val idGroup = grp.id.toString()

        if(grp.userIdList[Firebase.auth.uid] == "no"){
            holder.buttonAccept.visibility = View.VISIBLE
            holder.buttonAccept.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            holder.buttonRefuse.visibility = View.VISIBLE
            holder.buttonRefuse.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            if (context != null) {
                holder.sidebarView.background = ColorDrawable(context.getColor(R.color.orange))
            }

            holder.buttonAccept.setOnClickListener{
                database.child("groups").child(grp.id.toString()).child("userIdList").child(Firebase.auth.uid.toString()).setValue("yes")
                holder.buttonAccept.visibility = View.GONE
                holder.buttonRefuse.visibility = View.GONE
                val toUpdate = data[position]
                toUpdate.userIdList[Firebase.auth.uid.toString()] = "yes"
                data[position] = toUpdate
                notifyDataSetChanged()
                notifyItemChanged(position)
                notifyItemRangeChanged(position,1)
            }

            holder.buttonRefuse.setOnClickListener{
                database.child("groups").child(grp.id.toString()).child("userIdList").child(Firebase.auth.uid.toString()).removeValue()
                holder.buttonAccept.visibility = View.GONE
                holder.buttonRefuse.visibility = View.GONE
                data.removeAt(position)
                notifyItemRemoved(position)
            }

        }
        else {
            if (context != null) {
                holder.sidebarView.background = ColorDrawable(context.getColor(R.color.colorPrimaryDark))
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
        val sidebarView = binding.sidebarView
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