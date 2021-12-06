package com.example.taskyourtime.group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.taskyourtime.databinding.ActivityAddPublicationBinding
import com.example.taskyourtime.model.CalendarEvent
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.model.ToDoItem
import com.example.taskyourtime.services.GroupService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class AddPublicationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPublicationBinding
    private val groupService by inject<GroupService>()
    private val TAG = "AddPublicationActivity"
    private var database: DatabaseReference = Firebase.database.reference
    private var database2: DatabaseReference = Firebase.database.reference
    private var database3: DatabaseReference = Firebase.database.reference

    private val choices : MutableList<JvmType.Object> = mutableListOf<JvmType.Object>()
    private val notes : MutableList<Note> = mutableListOf<Note>()
    private val events : MutableList<CalendarEvent> = mutableListOf<CalendarEvent>()
    private val items : MutableList<ToDoItem> = mutableListOf<ToDoItem>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPublicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference.child("notes")
        val noteChildEventListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded::" + snapshot.key!!)
                val map = snapshot.value as Map<String?, Any?>
                val maNote = Note(map)
                maNote.id = snapshot.key!!
                //faire ce qu'on veut faire : afficher la note dans le recyclerView
                maNote.name?.let { Log.d(TAG, it) }
                if(Firebase.auth.uid == maNote.user_id){
                    notes.add(maNote)
                }
                binding?.recyclerView?.adapter?.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged:" + snapshot.key!!)
                val index = notes.indexOfFirst {it.id == snapshot.key!! } // -1 if not found
                if (index >= 0 && notes[index].user_id == Firebase.auth.uid){
                    val map = snapshot.value as Map<String?, Any?>
                    val maNote = Note(map)
                    val notId = notes[index].id
                    notes[index] = maNote
                    notes[index].id = notId
                    binding?.recyclerView?.adapter?.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + snapshot.key!!)
                val index = notes.indexOfFirst { it.id == snapshot.key!! } //-1 if not found
                if(index >= 0 && Firebase.auth.uid == notes[index].user_id){
                    val map = snapshot.value as Map<String?, Any?>
                    notes.removeAt(index)
                    binding?.recyclerView?.adapter?.notifyItemRemoved(index)
                    Toast.makeText(binding!!.root.context, "Note supprimée", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + snapshot.key!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", error.toException())
            }
        }


    }
}