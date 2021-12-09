package com.example.taskyourtime.services

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskyourtime.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

interface UserService {
    fun registerUser(firstname: String, lastname: String, email: String,
    password: String, profilePictureURI: String) : LiveData<Boolean>
    fun getUserById(userId: String) : LiveData<User?>
    fun loginUser(email: String, password: String) : LiveData<Boolean>
}

class UserServiceImpl(
    private val context: Context
) : UserService {
    private val TAG: String = "UserConnexionService"
    private val database : DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth

    init{
        auth.setLanguageCode("fr")
    }

    private fun firebaseUserToUser(user : FirebaseUser) : User {
        return User(
            "FirstName", "LastName", user.email, ""
        );
    }

    override fun registerUser(
        firstname: String,
        lastname: String,
        email: String,
        password: String,
        profilePictureURI: String
    ): LiveData<Boolean> {
        val success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")
                val newUser = auth.currentUser
                if (newUser != null) {
                    createUser(newUser.uid, firstname, lastname, email, profilePictureURI)
                    success.postValue(true);
                }
            } else {
                Log.w(TAG, "createUserWithEmail:failure", it.exception)
                success.postValue(false);
            }
        }
        return success
    }

    private fun createUser(userId: String, firstname: String, lastname: String,
                           email: String, profilePictureURI: String) : User {
        val user = User(firstname, lastname, email, profilePictureURI)
        database.child("users").child(userId).setValue(user)
        return user
    }

    private fun deleteUser(userId: String){
        database.child("users").child(userId).removeValue()
    }

    private fun updateUser(userId: String, field: String, newValue: String){
        database.child("users").child(userId).child(field).setValue(newValue)
    }


    override fun loginUser(email: String, password: String) : LiveData<Boolean> {
        val success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        if(email == "" ||password == ""){
            success.postValue(false)
        }
        else{
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() {
                if (it.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    success.postValue(true)
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", it.exception)
                    success.postValue(false)
                }
            }
        }
        return success
    }

    override fun getUserById(id: String): LiveData<User?> {
        var userResult: MutableLiveData<User?> = MutableLiveData<User?>()
        database.child("users").child(id).get().addOnSuccessListener {
            val map = it.value as Map<String?, Any?>
            val user = User("","","","")
            user.loadFromMap(map)
            userResult.postValue(user)
        }.addOnFailureListener {
            userResult.postValue(null)
        }
        return userResult
    }

}