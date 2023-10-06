package com.example.orientconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class WelcomeActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val register_welcome_btn = findViewById<Button>(R.id.register_welcome_btn)
        val login_welcome_btn = findViewById<Button>(R.id.login_welcome_btn)

        register_welcome_btn.setOnClickListener{
            val intent = Intent(this@WelcomeActivity,RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }

        login_welcome_btn.setOnClickListener{
            val intent = Intent(this@WelcomeActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()

        }



    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null)
        {
            val intent = Intent(this@WelcomeActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}