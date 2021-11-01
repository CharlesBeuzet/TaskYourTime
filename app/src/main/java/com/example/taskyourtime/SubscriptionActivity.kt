package com.example.taskyourtime

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var subscribe_button : Button
    lateinit var email_text : EditText
    lateinit var password_text : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        auth = Firebase.auth

        subscribe_button = findViewById(R.id.button_subscribe)
        email_text = findViewById(R.id.email)
        password_text = findViewById(R.id.password)

        subscribe_button.setOnClickListener(listener)
    }

    val listener = View.OnClickListener { view ->
        when(view.getId()) {
            R.id.button_subscribe -> {
                //Get the data and store it in Firebase
                val email = email_text.text.toString()
                val password = password_text.text.toString()
                Log.d("AUTH","Email : " + email + "password : " + password)

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            val intent = Intent(this, DefaultActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            //updateUI(null)
                        }
                    }

            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d("AUTH","User is ever signed-in ")
        }
    }




}

//TODO : get user first and last name, and picture