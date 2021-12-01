package com.example.taskyourtime.note

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityAddNoteBinding
import com.example.taskyourtime.services.NoteService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddNoteBinding
    private val noteService by inject<NoteService>()

    private fun buttonBold() {
        val spannable = SpannableStringBuilder(binding.noteContent.text)
        spannable.insert(binding.noteContent.selectionStart, "<b>")
        spannable.insert(binding.noteContent.selectionEnd + 3, "</b>")
        binding.noteContent.text = spannable
    }
    private fun buttonUnderline() {
        val spannable = SpannableStringBuilder(binding.noteContent.text)
        spannable.insert(binding.noteContent.selectionStart, "<u>")
        spannable.insert(binding.noteContent.selectionEnd + 3, "</u>")
        binding.noteContent.text = spannable
    }

    private fun buttonItalics(){
        val spannable = SpannableStringBuilder(binding.noteContent.text)
        spannable.insert(binding.noteContent.selectionStart, "<i>")
        spannable.insert(binding.noteContent.selectionEnd + 3, "</i>")
        binding.noteContent.text = spannable
    }

    private fun buttonNoFormat(){
        var temp = binding.noteContent.text.toString()
        var res = temp.replace("<b>", "", false)
        res = res.replace("</b>","", false)
        res = res.replace("<i>","", false)
        res = res.replace("</i>","", false)
        res = res.replace("<u>","", false)
        res = res.replace("</u>","", false)
        binding.noteContent.setText(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.boldButton.setOnClickListener{
            buttonBold()
        }

        binding.underlinedButton.setOnClickListener{
            buttonUnderline()
        }

        binding.italicButton.setOnClickListener{
            buttonItalics()
        }

        binding.buttonCancel.setOnClickListener{
            finish()
        }

        binding.noFormatButton.setOnClickListener{
            buttonNoFormat()
        }

        binding.buttonCreateNote.setOnClickListener{
            Log.d("Aenvoyer", "Le titre : " + binding.noteName.text.toString())
            Log.d("Aenvoyer", "Le titre : " + binding.noteContent.text.toString())

            val name = binding.noteName.text.toString()
            val content = binding.noteContent.text.toString()
            val userId = Firebase.auth.currentUser?.uid
            if (userId != null) {
                noteService.postNewNote(name, content, userId).observeForever{
                        success ->
                    if(success == true){
                        //fermer l'activity
                        Toast.makeText(binding.root.context, "Nouvelle note ajoutée", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}