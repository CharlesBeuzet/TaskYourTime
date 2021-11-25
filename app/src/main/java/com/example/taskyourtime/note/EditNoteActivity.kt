package com.example.taskyourtime.note

import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityEditNoteBinding
import com.example.taskyourtime.model.Note
import com.example.taskyourtime.services.NoteService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class EditNoteActivity : AppCompatActivity() {
    private var TAG = "EditNoteActivity"
    private lateinit var binding : ActivityEditNoteBinding
    private lateinit var note: Note
    private val noteService by inject<NoteService>()

    private fun buttonBold() {
        val spannable = SpannableStringBuilder(binding.editNoteContent.text)
        spannable.insert(binding.editNoteContent.selectionStart, "<b>")
        spannable.insert(binding.editNoteContent.selectionEnd + 3, "<b>")
        binding.editNoteContent.setText(spannable)
    }
    private fun buttonUnderline() {
        val spannable = SpannableStringBuilder(binding.editNoteContent.text)
        spannable.insert(binding.editNoteContent.selectionStart, "<u>")
        spannable.insert(binding.editNoteContent.selectionEnd + 3, "<u>")
        binding.editNoteContent.setText(spannable)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        note = intent.getSerializableExtra("noteToEdit") as Note;
        binding.editNoteName.setText(note.name.toString())
        binding.editNoteContent.setText(note.content.toString())
        //binding.editNoteContent.setText(Html.fromHtml(note.content,Html.FROM_HTML_MODE_LEGACY))
        Log.d(TAG, "${note.name}")

        binding.boldButton.setOnClickListener{
            buttonBold()
        }

        binding.underlinedButton.setOnClickListener{
            buttonUnderline()
        }

        binding.buttonEditCancel.setOnClickListener{
            finish()
        }

        binding.buttonEditNote.setOnClickListener{
            val name = binding.editNoteName.text.toString()
            val content = binding.editNoteContent.text.toString()
            val userId = Firebase.auth.currentUser?.uid
            if(userId != null){
                noteService.updateNote(note.id.toString(), name, content).observeForever{
                    success ->
                    if(success == true){
                        //fermer l'activity
                        Toast.makeText(binding.root.context, "Note éditée", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}