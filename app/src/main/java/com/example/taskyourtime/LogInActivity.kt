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
import com.example.taskyourtime.services.appModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

    class LogInActivity : AppCompatActivity() {

    private lateinit var loginButton : Button
    private lateinit var subscriptionLink : TextView
    private lateinit var emailText : EditText
    private lateinit var passwordText : EditText
    private lateinit var loginErrorText: TextView
    private lateinit var auth: FirebaseAuth

    private val userService by inject<UserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(appModule)
        }

        auth = Firebase.auth

        loginButton = findViewById(R.id.button_login)
        subscriptionLink = findViewById(R.id.subscription_link)
        emailText = findViewById(R.id.email)
        passwordText = findViewById(R.id.password)
        loginErrorText = findViewById(R.id.loginError)

        loginButton.setOnClickListener(listener)
        subscriptionLink.setOnClickListener(listener)
    }

    private val listener = View.OnClickListener { view ->
        when(view.id) {
            R.id.button_login -> {
                userService.loginUser(emailText.text.toString(), passwordText.text.toString()).observeForever{
                    success ->
                    if(success == true){
                        val intent = Intent(this, DefaultActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        loginErrorText.visibility = View.VISIBLE
                        loginErrorText.setText(R.string.login_error)
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
    }


}