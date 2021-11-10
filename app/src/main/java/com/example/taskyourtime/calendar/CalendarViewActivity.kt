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

class CalendarViewActivity : Fragment() {

    private var binding: ActivityCalendarViewBinding? = null

    private val calendarService by inject<CalendarService>()

    private val _binding get() = binding!!

    private val TAG = "CalendarViewActivity"
    private lateinit var database: DatabaseReference
    private val calendarEvents : MutableList<CalendarEvent> = mutableListOf<CalendarEvent>()
    private val userService by inject<UserService>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = ActivityCalendarViewBinding.inflate(inflater, container, false)
        val view = _binding.root
        return view
    }

    private fun displayEvents(){
        Log.d(TAG,"displayEvents")
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = CalendarViewAdapter(calendarEvents, calendarService)
        binding?.loaderFeed?.isVisible = false
    }

    override fun onStop(){
        super.onStop()
        //invalidateOptionsMenu()
        Log.i(TAG, "ACTIVITY SOPPED")
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
        binding?.addEventButton?.setOnClickListener{
            Log.d("CLICK", "Adding event")
            val intentAddCalendarEvent = Intent(context, AddToCalendarActivity::class.java)
            startActivity(intentAddCalendarEvent)

        }

        database = Firebase.database.reference.child("calendarEvents")
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded::" + snapshot.key!!)
                val map = snapshot.value as Map<String?, Any?>
                val myEvent = CalendarEvent(map)
                myEvent.id = snapshot.key!!

                myEvent.name?.let { Log.d(TAG, it) }
                if (Firebase.auth.uid == myEvent.user_id) {
                    calendarEvents.add(myEvent)
                }
                binding?.recyclerView?.adapter?.notifyDataSetChanged()
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
        database.addChildEventListener(childEventListener)

        displayEvents()
    }
}