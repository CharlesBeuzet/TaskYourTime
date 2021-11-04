package com.example.taskyourtime

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityListNoteBinding
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.services.NoteService
import com.google.firebase.database.DatabaseReference
import org.koin.android.ext.android.inject

class ListNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListNoteBinding

    private val noteService by inject<NoteService>()

    private val TAG = "ListNoteActivity"
    private lateinit var database: DatabaseReference
    private val notes : MutableList<Note> = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}