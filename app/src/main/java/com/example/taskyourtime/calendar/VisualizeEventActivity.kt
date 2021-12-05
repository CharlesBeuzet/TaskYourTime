package com.example.taskyourtime.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.ActivityVisualizeEventBinding
import com.example.taskyourtime.model.CalendarEvent
import com.example.taskyourtime.services.CalendarService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class VisualizeEventActivity: AppCompatActivity() {

    private var TAG = "VisualizeEventActivity"
    private lateinit var binding : ActivityVisualizeEventBinding
    private lateinit var event: CalendarEvent
    private lateinit var database: DatabaseReference

    private val eventService by inject<CalendarService>()

    private var cal: Calendar = Calendar.getInstance()
    private lateinit var beginDateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var endDateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var beginTimeSetListener: TimePickerDialog.OnTimeSetListener
    private lateinit var endTimeSetListener: TimePickerDialog.OnTimeSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizeEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        event = intent.getSerializableExtra("eventClicked") as CalendarEvent

        database = Firebase.database.reference.child("calendarEvents").child(event.id.toString())
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildChanged:" + snapshot.key!!)
                if(event.id == snapshot.key!!){
                    val map = snapshot.value as Map<String?, Any?>
                    event = CalendarEvent(map)
                    event.id = snapshot.key!!
                    binding.eventName.setText(event.name)
                    binding.eventDescription.setText(event.description)
                    binding.eventBeginDate.text = event.begin_date?.split(" ")?.get(0)
                    binding.eventBeginTime.text = event.begin_date?.split(" ")?.get(1)
                    binding.eventEndDate.text = event.end_date?.split(" ")?.get(0)
                    binding.eventEndTime.text = event.end_date?.split(" ")?.get(1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Unknown error")
            }

        }
        database.addValueEventListener(valueEventListener)

        binding.eventName.setText(event.name)
        binding.eventDescription.setText(event.description)
        binding.eventBeginDate.text = event.begin_date?.split(" ")?.get(0)
        binding.eventBeginTime.text = event.begin_date?.split(" ")?.get(1)
        binding.eventEndDate.text = event.end_date?.split(" ")?.get(0)
        binding.eventEndTime.text = event.end_date?.split(" ")?.get(1)

        binding.buttonCancel.setOnClickListener{
            finish()
        }

        beginDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            binding.eventBeginDate.text = String.format(getString(R.string.date_picker_placeholder),dayOfMonth, (monthOfYear + 1), year)
        }

        endDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            binding.eventEndDate.text = String.format(getString(R.string.date_picker_placeholder),dayOfMonth, (monthOfYear + 1), year)
        }

        beginTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            binding.eventBeginTime.text = String.format(getString(R.string.time_picker_placeholder),hourOfDay, minute)
        }

        endTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            binding.eventEndTime.text = String.format(getString(R.string.time_picker_placeholder),hourOfDay, minute)
        }

        binding.buttonEditEvent.setOnClickListener{
            val name = binding.eventName.text.toString()
            val description = binding.eventDescription.text.toString()
            val beginDate = binding.eventBeginDate.text.toString()
            val endDate = binding.eventEndDate.text.toString()
            val beginTime = binding.eventBeginTime.text.toString()
            val endTime = binding.eventEndTime.text.toString()
            val beginDateTime = "$beginDate $beginTime"
            val endDateTime = "$endDate $endTime"

            if(name.isBlank() || beginDate.isBlank() || beginTime.isBlank() || endDate.isBlank() || endTime.isBlank()){
                binding.tvError.text = getString(R.string.validation_error_message)
                binding.tvError.visibility = View.VISIBLE
            }
            else if(!checkDateTimeValidity(beginDateTime, endDateTime)){
                binding.tvError.text = getString(R.string.date_order_error_message)
                binding.tvError.visibility = View.VISIBLE
            }
            else {
                val userId = Firebase.auth.currentUser?.uid

                if (userId != null) {
                    eventService.updateCalendarEvent(
                        event.id.toString(),
                        name,
                        description,
                        beginDateTime,
                        endDateTime
                    ).observeForever { success ->
                        if (success == true) {
                            finish()
                        }
                        else{
                            binding.tvError.text = getString(R.string.add_error_message)
                            binding.tvError.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        binding.eventBeginDate.setOnClickListener {
            showDatePickerDialog(beginDateSetListener)
        }

        binding.eventEndDate.setOnClickListener {
            showDatePickerDialog(endDateSetListener)
        }

        binding.eventBeginTime.setOnClickListener{
            showTimePickerDialog(beginTimeSetListener)
        }

        binding.eventEndTime.setOnClickListener{
            showTimePickerDialog(endTimeSetListener)
        }
    }

    private fun showDatePickerDialog(selectedDateSetListener: DatePickerDialog.OnDateSetListener){
        val datePickerDialog = DatePickerDialog(
            this,
            selectedDateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH),
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(selectedTimeSetListener: TimePickerDialog.OnTimeSetListener){
        val timePickerDialog = TimePickerDialog(
            this,
            selectedTimeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun checkDateTimeValidity(beginDateTime: String, endDateTime: String): Boolean{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        val beginDateTimeFormatted = LocalDateTime.parse(beginDateTime, formatter);
        val endDateTimeFormatted = LocalDateTime.parse(endDateTime, formatter);
        return beginDateTimeFormatted <= endDateTimeFormatted
    }
}