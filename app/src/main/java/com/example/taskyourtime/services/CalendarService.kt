package com.example.taskyourtime.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskyourtime.model.CalendarEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.processNextEventInCurrentThread
import java.util.*

interface CalendarService{
    fun postNewCalendarEvent(name: String, description: String, beginDate: String, endDate: String, user_id: String): LiveData<Boolean?>
    fun deleteCalendarEvent(id: String): LiveData<Boolean?>
    fun findCalendarEventById(id: String) : LiveData<CalendarEvent?>
    fun updateCalendarEvent(id: String, newName: String, newDescription: String, newBeginDate: String, newEndDate: String) : LiveData<Boolean?>
}

class CalendarServiceImpl(
    private val context: Context
) : CalendarService{
    private val database: DatabaseReference = Firebase.database.reference
    private val TAG: String = "CalendarService"

    override fun postNewCalendarEvent(name: String, description: String, beginDate: String, endDate: String, user_id: String): MutableLiveData<Boolean>{
        var success: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val owner = Firebase.auth.currentUser
        if(owner != null){
            create(name, description, beginDate, endDate, owner.uid)
            Log.w(TAG, "SUCCESS : '$name' posted")
            success.postValue(true)
        }else{
            success.postValue(false)
            Log.w(TAG, "Error while event creation")
        }
        return success
    }

    override fun deleteCalendarEvent(id: String): LiveData<Boolean?> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var calendarEventToDelete = findCalendarEventById(id)
        if(calendarEventToDelete != null){
            delete(id)
            Log.w(TAG, "Delete processed with success '$id'")
            success.postValue(true)
        }else{
            success.postValue(false)
            Log.w(TAG, "Error while event deleting")
        }
        return success
    }

    override fun updateCalendarEvent(
        id: String,
        newName: String,
        newDescription: String,
        newBeginDate: String,
        newEndDate: String
    ): LiveData<Boolean?> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var calendarEventToUpdate = findCalendarEventById(id)
        if(calendarEventToUpdate != null){
            if(newName != null){
                updateField(id, "name", newName)
            }
            if(newDescription != null){
                updateField(id, "description", newDescription)
            }
            if(newBeginDate != null){
                updateField(id, "begin_date", newBeginDate)
            }
            if(newEndDate != null){
                updateField(id, "end_date", newEndDate)
            }
            Log.w(TAG, "Event updated")
            success.postValue(true)
        }else{
            Log.w(TAG, "Error while updating event'$id'")
            success.postValue(false)
        }
        return success
    }

    override fun findCalendarEventById(id: String): LiveData<CalendarEvent?> {
        var calendarEventResult: MutableLiveData<CalendarEvent?> = MutableLiveData<CalendarEvent?>()
        database.child("calendarEvents").child(id).get().addOnSuccessListener {
            val map = it.value as Map<String?, Any?>
            val calendarEvent = CalendarEvent("", "", "", "", "", "")
            calendarEvent.loadFromMap(map)
            Log.d(TAG, "Event found")
            calendarEventResult.postValue(calendarEvent)
        }.addOnFailureListener{
            Log.d(TAG, "Event not found")
            calendarEventResult.postValue(null)
        }
        return calendarEventResult
    }

    private fun create(name: String, description: String, beginDate: String, endDate: String, user_id: String){
        val key = database.child("calendarEvents").push().key
        if(key == null){
            Log.w(TAG, "Couldn't get push key for calendarEvents")
            return
        }
        Log.d(TAG, "Success in getting the key : $key")
        val calendarEvent : CalendarEvent = CalendarEvent(key, name, description, beginDate, endDate, user_id)
        database.child("calendarEvents").child(key).setValue(calendarEvent.toMap())
    }

    private fun delete(id: String){
        database.child("calendarEvents").child(id).removeValue()
        Log.d(TAG, "calendarEvent with id : $id has been deleted")
    }

    private fun updateField(id: String, field: String, newValue: Any){
        database.child("calendarEvents").child(id).child(field).setValue(newValue)
    }
}