package com.example.taskyourtime.productivity

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskyourtime.databinding.ActivityProductivityBinding


class ProductivityActivity : Fragment() {

    private var binding: ActivityProductivityBinding? = null

    private val _binding get() = binding!!
    private lateinit var notificationManager : NotificationManager
    private lateinit var chronometer : Chronometer

    private val TAG = "ProductivityActivity"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = ActivityProductivityBinding.inflate(inflater, container, false)
        val view = _binding.root
        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        binding?.hours?.minValue = 0
        binding?.hours?.maxValue = 24
        binding?.minutes?.minValue = 0
        binding?.minutes?.maxValue = 59
        binding?.seconds?.minValue = 0
        binding?.seconds?.maxValue = 59
        binding?.chronometer?.format ="%s:%s"
        //binding?.chronometer?.isCountDown = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "ProductivityActivity")
        var  update_hour_value : Int = 0
        var update_minute_value : Int = 0
        var update_seconds_value : Int = 0

        binding?.cancelButton?.setOnClickListener{
            binding?.chronometer?.stop()
            binding?.hours?.value = 0
            binding?.minutes?.value = 0
            binding?.seconds?.value = 0
            if (checkNotificationPolicyAccess(notificationManager)){
                notificationManager.offDOD()
                Toast.makeText(activity, "Do Not Disturb turned off.", Toast.LENGTH_LONG).show()
            }
        }

        binding?.setTimer?.setOnClickListener{
            val hours = binding?.hours?.value
            val minutes = binding?.minutes?.value
            val seconds = binding?.seconds?.value
            if (hours != null) {
                if (minutes != null) {
                    if (seconds != null) {
                        binding?.chronometer?.base= SystemClock.elapsedRealtime() + (hours*3600000 + minutes*60000 + seconds*1000)
                    }
                }
            }
            binding?.chronometer?.start()
            if (checkNotificationPolicyAccess(notificationManager)){
                notificationManager.onDOD()
                Toast.makeText(activity, "Do Not Disturb turned on.", Toast.LENGTH_LONG).show()
            }
        }

        binding?.chronometer?.setOnChronometerTickListener {
            val time = binding?.chronometer?.text.toString().split(":")
            Log.d(TAG, time.size.toString())
            when(time.size) {
                2 -> {//min et s
                    Log.d(TAG,"TWO : "+ time[1])
                    update_hour_value = 0
                    update_minute_value = time[0].toInt()
                    update_seconds_value = time[1].toInt()
                }
                3 -> {//h, mni et s
                    Log.d(TAG,"THREE"+ time[1]+ time[2] + time[0])
                    update_hour_value = time[0].toInt()
                    update_minute_value = time[1].toInt()
                    update_seconds_value = time[2].toInt()
                }
                else -> {
                    Log.d(TAG,"else")
                    update_minute_value = 0
                    update_hour_value = 0
                    update_seconds_value = 0
                }

            }
            Log.d(TAG,update_hour_value.toString() + " : " + update_minute_value.toString())
            binding?.hours?.value = update_hour_value
            binding?.minutes?.value = update_minute_value
            binding?.seconds?.value = update_seconds_value
            if(binding?.chronometer?.contentDescription.toString().equals("0 seconde")) {
                Log.d(TAG,"Chronometer stopped")
                binding?.chronometer?.stop()
                if (checkNotificationPolicyAccess(notificationManager)){
                    notificationManager.offDOD()
                    Toast.makeText(activity, "Do Not Disturb turned off.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Method to check notification policy access status
    private fun checkNotificationPolicyAccess(notificationManager:NotificationManager):Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted){
                Log.d(TAG,"Notification policy access granted.")
                return true
            }else{
                Toast.makeText(activity, "You need to grant notification policy access.", Toast.LENGTH_LONG).show()
                // If notification policy access not granted for this package
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }else{
            Toast.makeText(activity, "Device does not support this feature.", Toast.LENGTH_LONG).show()

        }
        return false
    }
}

// Extension function to turn on do not disturb
fun NotificationManager.onDOD(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
    }
}

// Extension function to turn off do not disturb
fun NotificationManager.offDOD(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
    }
}