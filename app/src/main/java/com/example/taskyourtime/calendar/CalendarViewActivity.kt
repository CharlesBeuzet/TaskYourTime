package com.example.taskyourtime.calendar

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.taskyourtime.databinding.ActivityCalendarViewBinding
import com.example.taskyourtime.model.CalendarEvent
import com.example.taskyourtime.services.CalendarService
import com.example.taskyourtime.services.UserService
import com.google.firebase.database.DatabaseReference
import org.koin.android.ext.android.inject

class CalendarViewActivity : Fragment() {

    private var binding: ActivityCalendarViewBinding? = null

    //private val calendarService by inject<CalendarService>()

    private val _binding get() = binding!!

    private val TAG = "CalendarViewActivity"
    private lateinit var database: DatabaseReference
    private val calendarEvents : MutableList<CalendarEvent> = mutableListOf<CalendarEvent>()
    //private val userService by inject<UserService>()

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
}