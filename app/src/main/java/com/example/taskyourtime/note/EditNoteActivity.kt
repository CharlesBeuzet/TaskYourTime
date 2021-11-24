package com.example.taskyourtime.note

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityAddNoteBinding
import com.example.taskyourtime.databinding.ActivityEditNoteBinding
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.services.NoteService
import org.koin.android.ext.android.inject

class EditNoteActivity : AppCompatActivity() {
    private final var TAG = "EditNoteActivity"
    private lateinit var binding : ActivityEditNoteBinding
    private lateinit var note: Note
    private val noteService by inject<NoteService>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        note = intent.getSerializableExtra("noteToEdit") as Note;
        binding.editNoteName.setText(note.name.toString())
        binding.editNoteContent.setText(note.content.toString())
        Log.d(TAG, "${note.name}")

        binding.buttonEditCancel.setOnClickListener{
            finish()
        }

        binding.buttonEditNote.setOnClickListener{

        }
    }
}