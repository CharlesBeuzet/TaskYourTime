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
            Log.d("Aevnoyer", "Le titre : " + binding.eventName.text.toString())
            Log.d("Aevnoyer", "La description : " + binding.eventDescription.text.toString())

            val name = binding.eventName.text.toString()
            val content = binding.eventDescription.text.toString()
            val user_id = Firebase.auth.currentUser?.uid
            if (user_id != null) {
                calendarService.postNewCalendarEvent(name, content, "", "", user_id).observeForever{
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