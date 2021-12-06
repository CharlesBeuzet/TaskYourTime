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
import java.util.regex.Pattern

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var subscribeButton : Button
    private lateinit var emailText : EditText
    private lateinit var passwordText : EditText
    private lateinit var firstnameText : EditText
    private lateinit var lastnameText : EditText
    private lateinit var errorText: TextView

    private var emailAddressPattern: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    )

    //private var passwordPattern: Pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")

    private val userService by inject<UserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        auth = Firebase.auth

        subscribeButton = findViewById(R.id.btnSignUp)
        emailText = findViewById(R.id.etEmail)
        passwordText = findViewById(R.id.etPassword)
        firstnameText = findViewById(R.id.etFirstName)
        lastnameText = findViewById(R.id.etLastName)
        errorText = findViewById(R.id.errorTV)

        subscribeButton.setOnClickListener(listener)
    }

    private val listener = View.OnClickListener { view ->
        when(view.id) {
            R.id.btnSignUp -> {
                //Get the data and store it in Firebase
                val email = emailText.text.toString()
                val password = passwordText.text.toString()
                val firstName = firstnameText.text.toString()
                val lastName = lastnameText.text.toString()

                if (email == "" || password == "" || firstName == "" || lastName == "") {
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(R.string.sign_up_error)
                } else if (!emailAddressPattern.matcher(email).matches()){
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(R.string.malformed_email_error)
                }
                else if(password.length < 6){
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(R.string.short_password_error)
                }
                /*else if(!passwordPattern.matcher(password).matches()){
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(R.string.password_validation_error)
                }*/
                else {
                    Log.d("AUTH", "Email : " + email + "password : " + password)

                    (userService.registerUser(firstName, lastName, email, password, "resources/picture")).observeForever { success ->
                        if (success == true) {
                            val intent = Intent(this, DefaultActivity::class.java)
                            startActivity(intent)
                        } else {
                            errorText.visibility = View.VISIBLE
                            errorText.setText(R.string.sign_up_content_error)
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
            Log.d("AUTH", "User is ever signed-in ")
        }
    }




}

//TODO : get user first and last name, and picture