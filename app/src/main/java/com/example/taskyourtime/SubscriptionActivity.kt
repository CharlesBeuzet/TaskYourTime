package com.example.taskyourtime

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taskyourtime.services.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var subscribe_button : Button
    lateinit var email_text : EditText
    lateinit var password_text : EditText
    lateinit var firstname_text : EditText
    lateinit var lastname_text : EditText
    lateinit var error_text: TextView

    private val userService by inject<UserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        auth = Firebase.auth

        subscribe_button = findViewById(R.id.btnSignUp)
        email_text = findViewById(R.id.etEmail)
        password_text = findViewById(R.id.etPassword)
        firstname_text = findViewById(R.id.etFirstName)
        lastname_text = findViewById(R.id.etLastName)
        error_text = findViewById(R.id.errorTV)

        subscribe_button.setOnClickListener(listener)
    }

    val listener = View.OnClickListener { view ->
        when(view.getId()) {
            R.id.btnSignUp -> {
                //Get the data and store it in Firebase
                val email = email_text.text.toString()
                val password = password_text.text.toString()
                val firstName = firstname_text.text.toString()
                val lastName = lastname_text.text.toString()

                if(email == "" || password == "" || firstName == "" || lastName == ""){
                    error_text.text = getString(R.string.sign_up_error)
                }
                else {
                    Log.d("AUTH", "Email : " + email + "password : " + password)

                    (userService.registerUser(firstName, lastName, email, password, "resources/picture" )).observeForever{
                        success ->
                        if(success == true){
                            val intent = Intent(this, DefaultActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            error_text.setText(R.string.sign_up_content_error)
                        }
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