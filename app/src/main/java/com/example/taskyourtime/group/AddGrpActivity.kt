package com.example.taskyourtime.group

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.databinding.AddGrpBinding
import com.example.taskyourtime.model.Group
import com.example.taskyourtime.model.User
import com.example.taskyourtime.services.GroupService
import com.example.taskyourtime.services.UserService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

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
        var bool = false
        binding.buttonAddUser.setOnClickListener{
            if(binding.userId.text.isNotEmpty()){//le champs d'edit text n'est pas vide
                Firebase.database.reference.child("users").child(binding.userId.text.toString()).get().addOnSuccessListener {
                    Log.d("firebase", "Got value ${it.value} from id ${binding.userId.text}")
                    if(!hashMap.containsKey(binding.userId.text.toString()) && it.value != null){
                        Log.d("deux", "${hashMap.toString()}")
                        hashMap[binding.userId.text.toString()] = "no"
                        Toast.makeText(this.applicationContext, "utilisateur ajouté", Toast.LENGTH_SHORT).show()
                        binding.userId.text.clear()
                    }
                    if(hashMap.containsKey(binding.userId.text.toString())){
                        Log.d("deux", "${hashMap.toString()}")
                        Toast.makeText(binding.root.context, "Vous avez déjà ajouté cet utilisateur", Toast.LENGTH_SHORT).show()
                        binding.userId.text.clear()
                    }
                    if(it.value == null){
                        Toast.makeText(this.applicationContext, "utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                        binding.userId.text.clear()
                    }
                }.addOnFailureListener{
                    Toast.makeText(this.applicationContext, "utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                    binding.userId.text.clear()
                    Log.d("firebase", "Error getting data", it)
                }
            }else{
                Toast.makeText(binding.root.context, "Le champs est vide", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCancel.setOnClickListener{
            finish()
        }

        binding.buttonAddGroup.setOnClickListener{
            if(binding.groupName.text.isNotEmpty()){
                groupService.createGroup(binding.groupName.text.toString(),Firebase.auth.uid.toString(),hashMap)
                finish()
            }else{
                Toast.makeText(binding.root.context, "champs nom du groupe vide.", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(binding.root.context, "Groupe créé", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        hashMap.clear()
    }
}