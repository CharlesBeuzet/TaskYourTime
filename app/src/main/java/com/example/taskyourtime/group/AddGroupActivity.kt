package com.example.taskyourtime.group

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityAddGroupBinding

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}