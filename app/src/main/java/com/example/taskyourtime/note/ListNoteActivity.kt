package com.example.taskyourtime.note

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskyourtime.DefaultActivity
import com.example.taskyourtime.databinding.ActivityListNoteBinding
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.services.NoteService
import com.example.taskyourtime.services.UserService
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class ListNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListNoteBinding

    private val noteService by inject<NoteService>()

    private val TAG = "ListNoteActivity"
    private lateinit var database: DatabaseReference
    private val notes : MutableList<Note> = mutableListOf<Note>()
    private val userService by inject<UserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        //go to activity create a note
        binding.addNoteButton.setOnClickListener{
            val intentAddNote = Intent(this, DefaultActivity::class.java)
            startActivity(intentAddNote)
        }

        displayNotes()

        //écoute les ajouts/modifications/... de notes
        database = Firebase.database.reference.child("notes")
        val childEventListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded::" + snapshot.key!!)
                val map = snapshot.value as Map<String?, Any?>
                val maNote = Note(map)
                maNote.id = snapshot.key!!
                //faire ce qu'on veut faire : afficher la note dans le recyclerView
                maNote.name?.let { Log.d(TAG, it) }
                notes.add(maNote)
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged:" + snapshot.key!!)
                val index = notes.indexOfFirst {it.id == snapshot.key!! } // -1 if not found
                if (index >= 0){
                    val map = snapshot.value as Map<String?, Any?>
                    val maNote = Note(map)
                    val notId = notes[index].id
                    notes[index] = maNote
                    notes[index] = maNote
                    notes[index].id = notId
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + snapshot.key!!)

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + snapshot.key!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", error.toException())
            }
        }
        database.addChildEventListener(childEventListener)


    }

    private fun displayNotes(){
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ListNoteAdapter(notes, userService)
        binding.loaderFeed.isVisible = false
    }

    override fun onStop(){
        super.onStop()
        invalidateOptionsMenu()
        Log.i(TAG, "ACTIVITY SOPPED")
    }

    override fun onDestroy() {
        super.onDestroy()
        invalidateOptionsMenu()
        Log.i(TAG, "ACTIVITY DESTROYED")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    /*override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_ -> {
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }*/
}