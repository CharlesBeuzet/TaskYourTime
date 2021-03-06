package com.example.taskyourtime.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.model.ToDoItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

interface ToDoItemService{
    fun postNewNote(content: String, user_id: String) : LiveData<Boolean?>
    fun deleteNote(id: String) : LiveData<Boolean?>
    fun findItemById(id: String) : LiveData<ToDoItem?>
    fun updateItem(id: String, checkValue: Boolean) : LiveData<Boolean?>
    fun updateItemPosition(id : String, newPosition : Int) : LiveData<Boolean?>
    //TODO : définir le check des éléments
}

class ToDoItemServiceImpl(
    private val context: Context
) : ToDoItemService {
    private val database: DatabaseReference = Firebase.database.reference
    private val TAG: String = "ToDoItemService"

    override fun postNewNote(content: String, user_id: String): MutableLiveData<Boolean> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val owner = Firebase.auth.currentUser
        if(owner != null){
            create(content, owner.uid)
            Log.w(TAG,"Success : '$content' posted")
            success.postValue(true)
        }else{
            success.postValue(false)
            Log.w(TAG, "Error while creating item")
        }
        return success
    }

    private fun create(content: String, user_id: String){
        val key = database.child("ItemToDoList").push().key
        if(key == null){
            Log.w(TAG, "Couldn't get push key for ToDoListItem")
            return
        }
        Log.d(TAG, "Success in getting the key: $key")
        getPositionAndSetData(content, user_id, key)
    }

    override fun deleteNote(id: String): LiveData<Boolean?> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var itemToDelete = findItemById(id)
        if(itemToDelete != null){
            delete(id)
            Log.w(TAG, "Success while item suppression '$id'")
        }else{
            success.postValue(false)
            Log.w(TAG, "Error while item suppression")
        }
        return success
    }

    private fun delete(id: String){
        database.child("ItemToDoList").child(id).removeValue()
        Log.d(TAG, "Item with id : $id has been deleted")
    }

    private fun getPositionAndSetData(content: String, user_id: String, key: String){
        var number : Long =0
        database.child("ItemToDoList").orderByChild("user_id").equalTo(user_id).get().addOnSuccessListener {
            number = it.childrenCount
            Log.d(TAG, "Number of items for this user : $number")
            val item : ToDoItem = ToDoItem(key, content, user_id,false,number+1)
            database.child("ItemToDoList").child(key).setValue(item.toMap())
        }.addOnFailureListener{
            Log.d(TAG,"Failed to get all the items for this user")
            number = 0
        }
    }

    override fun findItemById(id: String): LiveData<ToDoItem?> {
        var itemResult: MutableLiveData<ToDoItem?> = MutableLiveData<ToDoItem?>()
        database.child("ItemToDoList").child(id).get().addOnSuccessListener {
            val map = it.value as Map<String?, Any?>
            val item = ToDoItem("", "", "",false,0)
            item.loadFromMap(map)
            Log.d(TAG, "Item found")
            itemResult.postValue(item)
        }.addOnFailureListener{
            Log.d(TAG, "Item not found")
            itemResult.postValue(null)
        }
        return itemResult
    }

    private fun updateField(id: String, field: String, checkValue: Boolean){
        database.child("ItemToDoList").child(id).child(field).setValue(checkValue)
    }

    override fun updateItemPosition(id : String, newPosition : Int): LiveData<Boolean?>{
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        database.child("ItemToDoList").child(id).child("position").setValue(newPosition)
        success.postValue(true)
        return success
    }

    override fun updateItem(id: String, checkValue: Boolean): LiveData<Boolean?> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var noteToUpdate = findItemById(id)
        if(noteToUpdate != null){
            updateField(id, "done", checkValue)
            Log.w(TAG, "Item '$id' updated")
            success.postValue(true)
        }else{
            Log.w(TAG, "Error while item update '$id'")
            success.postValue(false)
        }
        return success
    }

}