package com.example.taskyourtime.group

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.AddGrpBinding
import com.example.taskyourtime.services.GroupService
import com.example.taskyourtime.services.UserService
import com.google.firebase.database.DatabaseReference
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class AddGrpActivity : AppCompatActivity() {

    private lateinit var binding: AddGrpBinding
    private val groupService by inject<GroupService>()
    private lateinit var database: DatabaseReference
    private val userService by inject<UserService>()
    private var hashMap: HashMap<String?, Any?> = HashMap<String?, Any?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddGrpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonAddUser.setOnClickListener{
            var userId = binding.userId.text
            var proof: String = "invalid"
            val user: Unit = userService.getUserById(userId.toString()).observeForever{
                success ->
                if(success != null){
                    //proof = success.email.toString()
                    if(!hashMap.containsKey(userId.toString()) && success != null){ //l'user id n'est pas encore dans le groupe et l'utilisateur existe en bdd
                        hashMap[userId.toString()] = "no"
                        binding.userId.text.clear()
                        Toast.makeText(binding.root.context, "L'utilisateur ${success.firstName} a été ajouté", Toast.LENGTH_SHORT).show()
                    }
                    else if(hashMap.containsKey(userId.toString())) {
                        Toast.makeText(binding.root.context, " Vous avez déjà ajouté cet utilisateur", Toast.LENGTH_LONG).show()
                        binding.userId.text.clear()
                    }else if(binding.userId.text.trim().isNotEmpty()){
                        Toast.makeText(binding.root.context, " Le champs est vide", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(binding.root.context, "Il n'y a pas d'utilisateur correspondant", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hashMap.clear()
    }
}