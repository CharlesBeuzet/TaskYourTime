package com.example.taskyourtime.group

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityAddGroupBinding
import com.example.taskyourtime.databinding.ActivityAddNoteBinding
import com.example.taskyourtime.services.GroupService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class AddGroupActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddGroupBinding
    private val groupService by inject<GroupService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /*binding.{
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
        }*/
    }
}