package com.example.taskyourtime.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.example.taskyourtime.databinding.ActivityVisualizeNoteBinding
import com.example.taskyourtime.model.Note

class VisualizeNoteActivity : AppCompatActivity() {
    private var TAG = "VisualizeNoteActivity"
    private lateinit var binding : ActivityVisualizeNoteBinding
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizeNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        note = intent.getSerializableExtra("noteClicked") as Note;
        binding.visualNoteName.text = Html.fromHtml(note.name, Html.FROM_HTML_MODE_LEGACY)
        binding.visualNoteContent.text = Html.fromHtml(note.content, Html.FROM_HTML_MODE_LEGACY)

        binding.buttonReturn.setOnClickListener{
            finish()
        }

        binding.buttonEditNote.setOnClickListener{
            val intentEditNote = Intent(this, EditNoteActivity::class.java)
            intentEditNote.putExtra("noteClicked",note)
            startActivity(intentEditNote)
        }
    }
}