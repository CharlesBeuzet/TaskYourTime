package com.example.taskyourtime.note

import android.os.Bundle
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
    }
}