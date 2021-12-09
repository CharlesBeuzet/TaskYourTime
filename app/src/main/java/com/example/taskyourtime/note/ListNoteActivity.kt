package com.example.taskyourtime.note

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskyourtime.databinding.ActivityListNoteBinding
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.services.NoteService
import com.example.taskyourtime.services.UserService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class ListNoteActivity : Fragment(), ListNoteAdapter.OnItemClickListener {

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
    ): View {
        binding = ActivityListNoteBinding.inflate(inflater, container, false)
        return _binding.root
    }

    private fun displayNotes(){
        Log.d(TAG,"displayNotes")
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = ListNoteAdapter(notes, noteService, this)
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
                    binding!!.recyclerView.adapter!!.notifyItemRemoved(index)
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
        database.addChildEventListener(childEventListener)

        displayNotes()
    }

    override fun onItemClick(position: Int) {
        val clikedItem = notes[position]
        val intentVisualizeNote = Intent(context, VisualizeNoteActivity::class.java)
        intentVisualizeNote.putExtra("noteClicked",clikedItem)
        startActivity(intentVisualizeNote)
    }
}