package com.example.taskyourtime.group

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.databinding.PublicationItemCellBinding
import com.example.taskyourtime.model.*
import com.example.taskyourtime.services.GroupService
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var binding: PublicationItemCellBinding

class PublicationPostAdapter(
    private val data: MutableList<Publication>,
    private val groupService: GroupService,
    ) : RecyclerView.Adapter<PublicationPostAdapter.PublicationPostHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PublicationPostHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = PublicationItemCellBinding.inflate(inflater, parent, false)
        return PublicationPostHolder(binding)
    }

    class PublicationPostHolder(binding: PublicationItemCellBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val name = binding.name
        val content = binding.content
        val beginDate = binding.beginDate
        val endDate = binding.endDate
        val publisher = binding.publisher
        val datePublication = binding.datePublication
        val layout = binding.root
    }

    override fun onBindViewHolder(holder: PublicationPostHolder, position: Int) {
        val data: Publication = data[position]
        val database = Firebase.database.reference

        holder.title.text = data.title
        holder.datePublication.text = "le " + data.datePublication
        if(data.type == "NOTE"){
            database.child("notes").child(data.noteId.toString()).get().addOnSuccessListener {
                val map = it.value as Map<String?, Any?>
                val maNote = Note(map)
                holder.name.text = maNote.name
                holder.content.text = Html.fromHtml(maNote.content,Html.FROM_HTML_MODE_LEGACY)
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }

        if(data.type == "CALENDAR"){
            database.child("calendarEvents").child(data.calendarEventId.toString()).get().addOnSuccessListener {
                val map = it.value as Map<String?, Any?>
                val monEvent = CalendarEvent(map)
                holder.name.text = monEvent.name
                holder.content.text = monEvent.description
                holder.beginDate.visibility = View.VISIBLE
                holder.beginDate.text = monEvent.begin_date
                holder.endDate.visibility = View.VISIBLE
                holder.endDate.text = monEvent.end_date
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }

        if(data.type == "TODO"){
            database.child("ItemToDoList").child(data.toDoListId.toString()).get().addOnSuccessListener {
                val map = it.value as Map<String?, Any?>
                val monItem = ToDoItem(map)
                holder.name.visibility = View.GONE
                holder.content.text = Html.fromHtml(monItem.content,Html.FROM_HTML_MODE_LEGACY)
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }

        database.child("users").child(data.userPublisherId.toString()).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val uMap = it.value as Map<String?, Any?>
            val user = User(uMap)
            holder.publisher.text = "publié par : " + user.firstName
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}