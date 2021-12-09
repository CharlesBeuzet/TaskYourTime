package com.example.taskyourtime.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.ActivityAddCalendarEventBinding
import com.example.taskyourtime.services.CalendarService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddToCalendarActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAddCalendarEventBinding
    private val calendarService by inject<CalendarService>()

    private var cal: Calendar = Calendar.getInstance()
    private lateinit var beginDateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var endDateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var beginTimeSetListener: TimePickerDialog.OnTimeSetListener
    private lateinit var endTimeSetListener: TimePickerDialog.OnTimeSetListener

    private var preFilledBeginDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCalendarEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.apply {
            if (hasExtra("SELECTED_DATE")) {
                preFilledBeginDate = getSerializableExtra("SELECTED_DATE") as? LocalDate
            }
        }

        binding.buttonCancel.setOnClickListener{
            finish()
        }

        binding.eventBeginDate.text = preFilledBeginDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()


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

        binding.buttonCreateEvent.setOnClickListener{
            Log.d("SEND", "Title : " + binding.eventName.text.toString())
            Log.d("SEND", "Description : " + binding.eventDescription.text.toString())
            Log.d("SEND", "Begin DateTime : " + binding.eventBeginDate.text.toString() + " à " + binding.eventBeginTime.text.toString())
            Log.d("SEND", "End DateTime : " + binding.eventEndDate.text.toString() + " à " + binding.eventEndTime.text.toString())

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
                    calendarService.postNewCalendarEvent(name, description, beginDateTime, endDateTime, userId).observeForever { success ->
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
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val beginDateTimeFormatted = LocalDateTime.parse(beginDateTime, formatter)
        val endDateTimeFormatted = LocalDateTime.parse(endDateTime, formatter)
        return beginDateTimeFormatted <= endDateTimeFormatted
    }
}