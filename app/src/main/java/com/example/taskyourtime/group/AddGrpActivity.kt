package com.example.taskyourtime.group

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.AddGrpBinding
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
    private var database: DatabaseReference = Firebase.database.reference
    private val userService by inject<UserService>()
    private var hashMap: HashMap<String?, Any?> = HashMap<String?, Any?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddGrpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAddUser.setOnClickListener{
            if(binding.userId.text.isNotEmpty()){
                database.child("users").orderByChild("email").equalTo(binding.userId.text.toString()).get().addOnSuccessListener {
                    Log.d("firebase", "Got value ${it.value} from email ${binding.userId.text}")
                    val u = it.value as Map<String?, Any?>
                    val firstName = it.child(u.keys.first().toString()).child("firstName").value
                    val lastName = it.child(u.keys.first().toString()).child("lastName").value
                    val email = it.child(u.keys.first().toString()).child("email").value
                    val profilePicture = it.child(u.keys.first().toString()).child("profilePicture").value
                    val us = User(firstName.toString(), lastName.toString(), email.toString(),profilePicture.toString())
                    val id = u.keys.first()
                    if(!hashMap.containsKey(id) && it.value != null){
                        Log.d("un", hashMap.toString())
                        hashMap[id.toString()] = "no"
                        Toast.makeText(this.applicationContext, "User added", Toast.LENGTH_SHORT).show()
                        if(binding.added.text.isEmpty()){
                            Log.d("le prénom", "${us.firstName}")
                            binding.added.text = us.firstName
                        }else{
                            Log.d("le prénom", "${us.firstName}")
                            val newAdding = binding.added.text.toString() + "\n" + us.firstName
                            binding.added.text = newAdding
                        }
                        binding.userId.text.clear()
                    }
                    if(hashMap.containsKey(id)){
                        binding.tvError.text = getString(R.string.redundant_user)
                        binding.userId.text.clear()
                    }
                    if(it.value == null){
                        binding.tvError.text = getString(R.string.user_not_find_error)
                        binding.userId.text.clear()
                    }
                }.addOnFailureListener{
                    binding.tvError.text = getString(R.string.user_not_find_error)
                    binding.userId.text.clear()
                    Log.d("firebase", "Error getting data", it)
                }
            }
        }

        binding.buttonCancel.setOnClickListener{
            finish()
        }

        binding.buttonAddGroup.setOnClickListener{
            var empty = false
            if(hashMap.size == 0){
                empty = true
            }

            if(binding.groupName.text.isNotEmpty() && hashMap.size > 0){
                groupService.createGroup(binding.groupName.text.toString(),Firebase.auth.uid.toString(),hashMap)
                finish()
            }else{
                if(empty){
                    binding.tvError.text = getString(R.string.no_user_error)
                }else{
                    binding.tvError.text = getString(R.string.group_name_error)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hashMap.clear()
    }
}