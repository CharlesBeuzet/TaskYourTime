package com.example.taskyourtime.group

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.ActivityAddGroupBinding
import com.example.taskyourtime.services.GroupService
import com.google.firebase.database.DatabaseReference
import org.koin.android.ext.android.inject

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding
    private val groupService by inject<GroupService>()
    private lateinit var database: DatabaseReference
    private val hashMap: HashMap<String?, Any?> = HashMap<String?, Any?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //var group: Group = Group("","", "", "")
        binding.buttonCancel.setOnClickListener{
            finish()
        }

        binding.buttonInviteUser.setOnClickListener {
            var userId = binding.userId.text
            if(!hashMap.containsKey(userId.toString())){
                hashMap[userId.toString()] = "no"
                //binding.userId.setText("")
            }else{
                Toast.makeText(binding.root.context, "Vous avez déjà ajouté cet utilisateur", Toast.LENGTH_SHORT).show()
                //binding.userId.setText("")
            }
        }

    }
}