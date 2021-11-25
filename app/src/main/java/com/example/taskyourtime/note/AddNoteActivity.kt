package com.example.taskyourtime.note

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCancel.setOnClickListener{
            finish()
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