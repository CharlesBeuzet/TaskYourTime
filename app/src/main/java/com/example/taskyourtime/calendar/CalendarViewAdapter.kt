package com.example.taskyourtime.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.databinding.CalendarEventCellBinding
import com.example.taskyourtime.model.CalendarEvent
import com.example.taskyourtime.services.CalendarService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private lateinit var binding: CalendarEventCellBinding

class CalendarViewAdapter(
        private var data: MutableList<CalendarEvent>,
        private val calendarService: CalendarService,
) : RecyclerView.Adapter<CalendarViewAdapter.CalendarViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = CalendarEventCellBinding.inflate(inflater, parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val data: CalendarEvent = data[position]
        Log.d("onBindViewHolder", "${data.name}" +" "+ "${data.begin_date}")
        holder.nameEvent.text = data.name
        holder.descriptionEvent.text = data.description
        holder.beginDateTimeEvent.text = data.begin_date
        holder.endDateTimeEvent.text = data.end_date
        val id_event = data.id.toString()
        holder.deleteEventButton.setOnClickListener{
            Log.d("Asupprimer", "Evennement dont l'id est $id_event supprimé")
            calendarService.deleteCalendarEvent(id_event)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class CalendarViewHolder(binding: CalendarEventCellBinding) : RecyclerView.ViewHolder(binding.root){
        val nameEvent = binding.nameEvent
        val descriptionEvent = binding.descriptionEvent
        val beginDateTimeEvent = binding.beginDateTimeEvent
        val endDateTimeEvent = binding.endDateTimeEvent
        val deleteEventButton = binding.deleteEventButton
        val layout = binding.root
    }
}