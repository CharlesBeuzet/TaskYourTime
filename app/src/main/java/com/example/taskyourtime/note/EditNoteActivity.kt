package com.example.taskyourtime.note

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
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
        spannable.insert(binding.editNoteContent.selectionEnd + 3, "</b>")
        binding.editNoteContent.text = spannable
    }
    private fun buttonUnderline() {
        val spannable = SpannableStringBuilder(binding.editNoteContent.text)
        spannable.insert(binding.editNoteContent.selectionStart, "<u>")
        spannable.insert(binding.editNoteContent.selectionEnd + 3, "</u>")
        binding.editNoteContent.text = spannable
    }

    private fun buttonItalics(){
        val spannable = SpannableStringBuilder(binding.editNoteContent.text)
        spannable.insert(binding.editNoteContent.selectionStart, "<i>")
        spannable.insert(binding.editNoteContent.selectionEnd + 3, "</i>")
        binding.editNoteContent.text = spannable
    }

    private fun buttonNoFormat(){
        var temp = binding.editNoteContent.text.toString()
        var res = temp.replace("<b>", "", false)
        res = res.replace("</b>","", false)
        res = res.replace("<i>","", false)
        res = res.replace("</i>","", false)
        res = res.replace("<u>","", false)
        res = res.replace("</u>","", false)
        binding.editNoteContent.setText(res)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        note = intent.getSerializableExtra("noteClicked") as Note;
        binding.editNoteName.setText(note.name.toString())
        binding.editNoteContent.setText(note.content.toString())
        Log.d(TAG, "${note.name}")

        binding.boldButton.setOnClickListener{
            buttonBold()
        }

        binding.underlinedButton.setOnClickListener{
            buttonUnderline()
        }

        binding.underlinedButton.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.italicButton.setOnClickListener{
            buttonItalics()
        }

        binding.noFormatButton.setOnClickListener{
            buttonNoFormat()
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
                        finish()
                    }
                }
            }
        }
    }
}