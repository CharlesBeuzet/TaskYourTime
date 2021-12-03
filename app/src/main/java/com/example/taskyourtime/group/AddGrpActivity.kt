package com.example.taskyourtime.group

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.AddGrpBinding
import com.example.taskyourtime.services.GroupService
import com.google.firebase.database.DatabaseReference
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class AddGrpActivity : AppCompatActivity() {

    private lateinit var binding: AddGrpBinding
    private val groupService by inject<GroupService>()
    private lateinit var database: DatabaseReference
    private val hashMap: HashMap<String?, Any?> = HashMap<String?, Any?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddGrpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAddUser.setOnClickListener{
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