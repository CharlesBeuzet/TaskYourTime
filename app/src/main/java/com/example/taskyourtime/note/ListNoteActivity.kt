package com.example.taskyourtime.note

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskyourtime.DefaultActivity
import com.example.taskyourtime.R
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
import org.koin.java.KoinJavaComponent.inject

class ListNoteActivity : Fragment() {

    private var binding: ActivityListNoteBinding? = null

    private val noteService by inject<NoteService>()

    private val _binding get() = binding!!

    private val TAG = "ListNoteActivity"
    private lateinit var database: DatabaseReference
    private val notes : MutableList<Note> = mutableListOf<Note>()
    private val userService by inject<UserService>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding = ActivityListNoteBinding.inflate(inflater, container, false)
            val view = _binding.root
            return view
            }

    private fun displayNotes(){
        Log.d(TAG,"displayNotes")
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = ListNoteAdapter(notes)
        binding?.loaderFeed?.isVisible = false
    }

    override fun onStop(){
        super.onStop()
        //invalidateOptionsMenu()
        Log.i(TAG, "ACTIVITY SOPPED")
    }

    override fun onDestroy() {
        super.onDestroy()
        //invalidateOptionsMenu()
        Log.i(TAG, "ACTIVITY DESTROYED")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG,"Fragment started")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"ListeNoteView created")
        super.onViewCreated(view, savedInstanceState)
        Log.d("Note Creation", "Inside the createview of notes")
        //go to activity create a note
        binding?.addNoteButton?.setOnClickListener{
            Log.d("CLICK","Adding Note")
            val intentAddNote = Intent(context, AddNoteActivity::class.java)
            startActivity(intentAddNote)
        }

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
                binding?.recyclerView?.adapter?.notifyDataSetChanged()
                Log.d(TAG,"View updated")
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
                    binding?.recyclerView?.adapter?.notifyDataSetChanged()
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

        displayNotes()
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_ -> {
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }*/
}