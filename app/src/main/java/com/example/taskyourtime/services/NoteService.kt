package com.example.taskyourtime.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskyourtime.model.Note
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

interface NoteService{
    fun postNewNote(name: String, content: String, user_id: String) : LiveData<Boolean?>
    fun deleteNote(id: String) : LiveData<Boolean?>
    fun findNoteById(id: String) : LiveData<Note?>
    fun updateNote(id: String, newName: String, newContent: String) : LiveData<Boolean?>
}

class NoteServiceImpl(
    private val context: Context
) : NoteService{
    private val database: DatabaseReference = Firebase.database.reference
    private val TAG : String = "NoteService"

    override fun postNewNote(name: String, content: String, user_id: String): MutableLiveData<Boolean> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val owner = Firebase.auth.currentUser
        if(owner != null){
            create(name, content, owner.uid)
            Log.w(TAG,"Succès : '$name' postée")
            success.postValue(true)
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de la création de la note")
        }
        return success
    }

    override fun deleteNote(id: String): LiveData<Boolean?> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var noteToDelete = findNoteById(id)
        if(noteToDelete != null){
            delete(id)
            Log.w(TAG, "Succès lors de la suppression de la note '$id'")
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de la suppression")
        }
        return success
    }

    override fun updateNote(id: String, newName: String, newContent: String): LiveData<Boolean?> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var noteToUpdate = findNoteById(id)
        if(noteToUpdate != null){
            if(newName != null){
                updateField(id, "name", newName)
            }
            if(newContent != null){
                updateField(id, "content", newContent)
            }
            Log.w(TAG, "Note '$id' mise à jour")
            success.postValue(true)
        }else{
            Log.w(TAG, "Erreur lors de la mise à jour de la note '$id'")
            success.postValue(false)
        }
        return success
    }

    override fun findNoteById(id: String): LiveData<Note?> {
        var noteResult: MutableLiveData<Note?> = MutableLiveData<Note?>()
        database.child("notes").child(id).get().addOnSuccessListener {
            val map = it.value as Map<String?, Any?>
            val note = Note("", "", "", "")
            note.loadFromMap(map)
            Log.d(TAG, "Note trouvée")
            noteResult.postValue(note)
        }.addOnFailureListener{
            Log.d(TAG, "Note non trouvée")
            noteResult.postValue(null)
        }
        return noteResult
    }

    private fun create(name: String, content: String, user_id: String){
        val key = database.child("notes").push().key
        if(key == null){
            Log.w(TAG, "Couldn't get push key for notes")
            return
        }
        Log.d(TAG, "Success in getting the key: $key")
        val note : Note = Note(key, name, content, user_id)
        database.child("notes").child(key).setValue(note.toMap())
    }

    private fun delete(id: String){
        database.child("notes").child(id).removeValue()
        Log.d(TAG, "Note dont l'id est : $id a été supprimée")
    }

    private fun updateField(id: String, field: String, newValue: String){
        database.child("notes").child(id).child(field).setValue(newValue)
    }

}