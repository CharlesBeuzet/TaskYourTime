package com.example.taskyourtime.calendar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.CalendarEventCellBinding
import com.example.taskyourtime.model.CalendarEvent
import com.example.taskyourtime.services.CalendarService

private lateinit var binding: CalendarEventCellBinding

class CalendarViewAdapter(
        private var data: MutableList<CalendarEvent>,
        private val calendarService: CalendarService,
        private val listener: OnItemClickListener,
        private val context: Context?,
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
        if(context != null) {
            holder.beginDateTimeEvent.text = String.format(context.getString(R.string.date_display_placeholder), data.begin_date?.substring(0,11), data.begin_date?.substring(11))
            holder.endDateTimeEvent.text = String.format(context.getString(R.string.date_display_placeholder), data.end_date?.substring(0,11), data.end_date?.substring(11))
        }
            val id_event = data.id.toString()
        if(data.description?.isEmpty() == true){
            holder.imageDescription.visibility = View.INVISIBLE
        }
        else{
            holder.imageDescription.visibility = View.VISIBLE
        }
        holder.deleteEventButton.setOnClickListener{
            Log.d("DELETE", "Evennement dont l'id est $id_event supprimé")
            calendarService.deleteCalendarEvent(id_event)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class CalendarViewHolder(binding: CalendarEventCellBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        val nameEvent = binding.nameEvent
        val beginDateTimeEvent = binding.beginDateTimeEvent
        val endDateTimeEvent = binding.endDateTimeEvent
        val deleteEventButton = binding.deleteEventButton
        val imageDescription = binding.imageDescription
        val layout = binding.root

        init{
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}