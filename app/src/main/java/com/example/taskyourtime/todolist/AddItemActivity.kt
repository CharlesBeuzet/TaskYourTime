package com.example.taskyourtime.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.ActivityAddItemBinding
import com.example.taskyourtime.databinding.ActivityAddNoteBinding
import com.example.taskyourtime.services.NoteService
import com.example.taskyourtime.services.ToDoItemService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddItemBinding
    private val itemService by inject<ToDoItemService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.addButton.setOnClickListener{
            Log.d("Item Content", binding.itemContent.text.toString())

            val content = binding.itemContent.text.toString()
            val user_id = Firebase.auth.currentUser?.uid
            if(binding.itemContent.text.isBlank()) {
                binding.tvError.text = getString(R.string.error_description)
                binding.tvError.visibility = View.VISIBLE
            }
            else{
                if (user_id != null) {
                    itemService.postNewNote(content, user_id).observeForever { success ->
                        if (success == true) {
                            finish()
                        }
                        else{
                            binding.tvError.text = getString(R.string.add_error_message)
                            binding.tvError.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}