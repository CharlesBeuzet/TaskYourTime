package com.example.taskyourtime.calendar

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityAddCalendarEventBinding
import com.example.taskyourtime.databinding.ActivityAddNoteBinding
import com.example.taskyourtime.services.CalendarService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import java.util.*

class AddToCalendarActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAddCalendarEventBinding
    private val calendarService by inject<CalendarService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCalendarEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCancel.setOnClickListener{
            finish()
        }

        binding.buttonCreateEvent.setOnClickListener{
            Log.d("Aenvoyer", "Le titre : " + binding.eventName.text.toString())
            Log.d("Aenvoyer", "La description : " + binding.eventDescription.text.toString())
            Log.d("Aenvoyer", "DateTime de debut : " + binding.eventBeginDate.text.toString() + " à " + binding.eventBeginTime.text.toString())
            Log.d("Aenvoyer", "Date de fin : " + binding.eventEndDate.text.toString() + " à " + binding.eventEndTime.text.toString())

            val name = binding.eventName.text.toString()
            val description = binding.eventDescription.text.toString()
            val beginDateTime = binding.eventBeginDate.text.toString() + " " + binding.eventBeginTime.text.toString()
            val endDateTime = binding.eventEndDate.text.toString() + " " + binding.eventEndTime.text.toString()

            val user_id = Firebase.auth.currentUser?.uid
            if (user_id != null) {
                calendarService.postNewCalendarEvent(name, description, beginDateTime, endDateTime, user_id).observeForever{
                    success ->
                    if(success == true){
                        //fermer l'activity
                        finish()
                    }
                }
            }
        }
    }
}