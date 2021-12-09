package com.example.taskyourtime.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import com.example.taskyourtime.databinding.ActivityVisualizeNoteBinding
import com.example.taskyourtime.model.Note
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class VisualizeNoteActivity : AppCompatActivity() {
    private var TAG = "VisualizeNoteActivity"
    private lateinit var binding : ActivityVisualizeNoteBinding
    private lateinit var note: Note
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizeNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        note = intent.getSerializableExtra("noteClicked") as Note;

        database = Firebase.database.reference.child("notes").child(note.id.toString())
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildChanged:" + snapshot.key!!)
                if(note.id == snapshot.key!!){
                    val map = snapshot.value as Map<String?, Any?>
                    note = Note(map)
                    note.id = snapshot.key!!
                    binding.visualNoteName.text = Html.fromHtml(note.name, Html.FROM_HTML_MODE_LEGACY)
                    binding.visualNoteContent.text = Html.fromHtml(note.content, Html.FROM_HTML_MODE_LEGACY)
                    binding.visualNoteContent.movementMethod = ScrollingMovementMethod()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Unknown error.")
            }

        }
        database.addValueEventListener(valueEventListener)


        binding.visualNoteName.text = Html.fromHtml(note.name, Html.FROM_HTML_MODE_LEGACY)
        binding.visualNoteContent.text = Html.fromHtml(note.content, Html.FROM_HTML_MODE_LEGACY)

        binding.buttonReturn.setOnClickListener{
            finish()
        }

        binding.buttonEditNote.setOnClickListener{
            val intentEditNote = Intent(this, EditNoteActivity::class.java)
            intentEditNote.putExtra("noteClicked", note)
            startActivity(intentEditNote)
        }
    }
}