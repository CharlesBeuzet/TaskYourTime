package com.example.taskyourtime.group

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityAddGroupBinding
import com.example.taskyourtime.services.GroupService
import org.koin.android.ext.android.inject

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding
    private val groupService by inject<GroupService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCancel.setOnClickListener{
            finish()
        }



    }
}