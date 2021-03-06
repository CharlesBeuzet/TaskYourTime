package com.example.taskyourtime.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.ActivityCalendarViewBinding
import com.example.taskyourtime.model.CalendarEvent
import com.example.taskyourtime.services.CalendarService
import com.example.taskyourtime.services.UserService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CalendarViewActivity : Fragment(), CalendarViewAdapter.OnItemClickListener {

    private var binding: ActivityCalendarViewBinding? = null

    private val calendarService by inject<CalendarService>()

    private val _binding get() = binding!!

    private val TAG = "CalendarViewActivity"
    private lateinit var database: DatabaseReference
    private val calendarEvents : MutableList<CalendarEvent> = mutableListOf()
    private val userService by inject<UserService>()

    var selectedDate : LocalDate = LocalDate.now()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = ActivityCalendarViewBinding.inflate(inflater, container, false)
        return _binding.root
    }

    private fun displayEvents(){
        Log.d(TAG,"displayEvents")
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = CalendarViewAdapter(calendarEvents, calendarService, this, context)
        binding?.loaderFeed?.isVisible = false
    }


    override fun onStop(){
        super.onStop()
        //invalidateOptionsMenu()
        Log.i(TAG, "ACTIVITY STOPPED")
    }

    override fun onDestroy() {
        super.onDestroy()
        //invalidateOptionsMenu()
        Log.i(TAG, "ACTIVITY DESTROYED")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG,"Fragment started")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "CalendarViewCreated")
        super.onViewCreated(view, savedInstanceState)
        Log.d("Event creation", "Inside the createview of events")

        binding?.tvDayInfo?.text = String.format(getString(R.string.info_day), selectedDate.dayOfWeek.toString().substring(0,1) + selectedDate.dayOfWeek.toString().substring(1).toLowerCase(), selectedDate.dayOfMonth.toString(), selectedDate.month.toString().substring(0,1) + selectedDate.month.toString().substring(1).toLowerCase(), selectedDate.year.toString())

        binding?.addEventButton?.setOnClickListener{
            Log.d("CLICK", "Adding event")
            val intentAddCalendarEvent = Intent(context, AddToCalendarActivity::class.java)
            intentAddCalendarEvent.putExtra("SELECTED_DATE", selectedDate)
            startActivity(intentAddCalendarEvent)
        }

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val map = snapshot.value as Map<String?, Any?>
                val myEvent = CalendarEvent(map)
                myEvent.id = snapshot.key!!
                if(myEvent.user_id == Firebase.auth.uid) {
                    if (checkDateValidity(myEvent.begin_date?.split(" ")?.get(0).toString(), myEvent.end_date?.split(" ")?.get(0).toString(), selectedDate)
                    ) {
                        Log.d(TAG, "onChildAdded::" + snapshot.key!!)
                        calendarEvents.add(myEvent)
                    }
                    binding?.recyclerView?.adapter?.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged:" + snapshot.key!!)
                val index = calendarEvents.indexOfFirst {it.id == snapshot.key!! } // -1 if not found
                if (index >= 0 && calendarEvents[index].user_id == Firebase.auth.uid){
                    val map = snapshot.value as Map<String?, Any?>
                    val myEvent = CalendarEvent(map)
                    val eventId = calendarEvents[index].id
                    calendarEvents[index] = myEvent
                    calendarEvents[index] = myEvent
                    calendarEvents[index].id = eventId
                    binding?.recyclerView?.adapter?.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + snapshot.key!!)
                val index = calendarEvents.indexOfFirst { it.id == snapshot.key!! } //-1 if not found
                if(index >= 0 && Firebase.auth.uid == calendarEvents[index].user_id){
                    val map = snapshot.value as Map<String?, Any?>
                    calendarEvents.removeAt(index)
                    binding?.recyclerView?.adapter?.notifyItemRemoved(index)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + snapshot.key!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", error.toException())
            }

        }

        binding?.calendarView?.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year
            selectedDate = LocalDate.of(year, month+1, dayOfMonth)
            Log.d(TAG, msg)

            binding?.tvDayInfo?.text = String.format(getString(R.string.info_day), selectedDate.dayOfWeek.toString().substring(0,1) + selectedDate.dayOfWeek.toString().substring(1).toLowerCase(), selectedDate.dayOfMonth.toString(), selectedDate.month.toString().substring(0,1) + selectedDate.month.toString().substring(1).toLowerCase(), selectedDate.year.toString())

            calendarEvents.clear()
            database = Firebase.database.reference.child("calendarEvents")
            database.removeEventListener(childEventListener)
            database.addChildEventListener(childEventListener)

            binding?.recyclerView?.adapter?.notifyDataSetChanged()
        }

        database = Firebase.database.reference.child("calendarEvents")
        database.addChildEventListener(childEventListener)

        displayEvents()

    }

    override fun onItemClick(position: Int) {
        val clickedItem = calendarEvents[position]
        Log.d(TAG, clickedItem.toString())
        val intentVisualizeEvent = Intent(context, VisualizeEventActivity::class.java)
        intentVisualizeEvent.putExtra("eventClicked", clickedItem)
        startActivity(intentVisualizeEvent)
    }

    private fun checkDateValidity(beginDate: String, endDate: String, currentDate: LocalDate): Boolean{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val beginDateTimeFormatted = LocalDate.parse(beginDate, formatter)
        val endDateTimeFormatted = LocalDate.parse(endDate, formatter)
        return beginDateTimeFormatted <= currentDate && currentDate <= endDateTimeFormatted
    }
}
