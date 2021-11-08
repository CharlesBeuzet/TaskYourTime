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

interface ToDoItemService{
    fun postNewNote(content: String, user_id: String) : LiveData<Boolean?>
    fun deleteNote(id: String) : LiveData<Boolean?>
    fun findItemById(id: String) : LiveData<ToDoItem?>
    //fun updateNote(id: String, newContent: String) : LiveData<Boolean?>
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
            Log.w(TAG,"Succès : '$content' posté en tant qu'item de la ToDoList")
            success.postValue(true)
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de la création de la note")
        }
        return success
    }

    private fun create(content: String, user_id: String){
        val key = database.child("ItemToDoList").push().key
        if(key == null){
            Log.w(TAG, "Couldn't get push key for todolistItem")
            return
        }
        Log.d(TAG, "Success in getting the key: $key")
        val item : ToDoItem = ToDoItem(key, content, user_id)
        database.child("ItemToDoList").child(key).setValue(item.toMap())
    }

    override fun deleteNote(id: String): LiveData<Boolean?> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var itemToDelete = findItemById(id)
        if(itemToDelete != null){
            delete(id)
            Log.w(TAG, "Succès lors de la suppression de l'item '$id'")
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de la suppression")
        }
        return success
    }

    private fun delete(id: String){
        database.child("ItemToDoList").child(id).removeValue()
        Log.d(TAG, "Item dont l'id est : $id a été supprimée")
    }

    override fun findItemById(id: String): LiveData<ToDoItem?> {
        var itemResult: MutableLiveData<ToDoItem?> = MutableLiveData<ToDoItem?>()
        database.child("ItemToDoList").child(id).get().addOnSuccessListener {
            val map = it.value as Map<String?, Any?>
            val item = ToDoItem("", "", "")
            item.loadFromMap(map)
            Log.d(TAG, "Item trouvé")
            itemResult.postValue(item)
        }.addOnFailureListener{
            Log.d(TAG, "Item non trouvée")
            itemResult.postValue(null)
        }
        return itemResult
    }

}