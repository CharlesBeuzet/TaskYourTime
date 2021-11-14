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
import com.example.taskyourtime.model.User
import com.example.taskyourtime.note.ListNoteActivity
import com.example.taskyourtime.services.UserService
import com.example.taskyourtime.services.appModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

    class LogInActivity : AppCompatActivity() {

    lateinit var login_button : Button
    lateinit var subscription_link : TextView
    lateinit var email_text : EditText
    lateinit var password_text : EditText
    lateinit var login_error_text: TextView
    private lateinit var auth: FirebaseAuth

    private val userService by inject<UserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Starting Koin
        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(appModule)
        }

        auth = Firebase.auth

        //get login button
        login_button = findViewById(R.id.button_login)
        subscription_link = findViewById(R.id.subscription_link)
        email_text = findViewById(R.id.email)
        password_text = findViewById(R.id.password)
        login_error_text = findViewById(R.id.loginError)

        login_button.setOnClickListener(listener)
        subscription_link.setOnClickListener(listener)
    }

    val listener = View.OnClickListener { view ->
        when(view.getId()) {
            R.id.button_login -> {
                userService.loginUser(email_text.text.toString(), password_text.text.toString()).observeForever{
                    success ->
                    if(success == true){
                        val intent = Intent(this, DefaultActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        login_error_text.setText(R.string.login_error)
                    }
                }
            }
            R.id.subscription_link -> {
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