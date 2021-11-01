    package com.example.taskyourtime

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.*

class LogInActivity : AppCompatActivity() {

    lateinit var login_button : Button
    lateinit var subscription_link : TextView
    lateinit var email_text : EditText
    lateinit var password_text : EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        //get login button
        login_button = findViewById(R.id.button_login)
        subscription_link = findViewById(R.id.subscription_link)
        email_text = findViewById(R.id.email)
        password_text = findViewById(R.id.password)

        login_button.setOnClickListener(listener)
        subscription_link.setOnClickListener(listener)
    }

    val listener = View.OnClickListener { view ->
        when(view.getId()) {
            R.id.button_login -> {
                //Get the data and send it to Firebase for check and connexion
                val email = email_text.text.toString()
                val password = password_text.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            val intent = Intent(this, DefaultActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

            }
            R.id.subscription_link -> {
                //Launch of the subscribtion activity
                val intent = Intent(this, SubscriptionActivity::class.java)
                startActivity(intent)
                Log.d("CLICK","Activity to subscribe launched")
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //reload();
        }
    }


}

//TODO : rename activity. This activity is not really the main