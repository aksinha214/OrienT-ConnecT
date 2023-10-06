package com.example.orientconnect

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar;

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""


    lateinit var usernameRegister: EditText
    lateinit var emailRegister: EditText
    lateinit var passwordRegister: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameRegister = findViewById(R.id.username_register)
        emailRegister = findViewById(R.id.email_register)
        passwordRegister = findViewById(R.id.password_register)
        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()

        }

        mAuth = FirebaseAuth.getInstance()

        val registerBtn = findViewById<Button>(R.id.register_btn)
        registerBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {


        val username: String = usernameRegister.text.toString()
        val email: String = emailRegister.text.toString()
        val password: String = passwordRegister.text.toString()

        if (username == "") {
            Toast.makeText(this@RegisterActivity, "Please write Username.", Toast.LENGTH_LONG)
                .show()
        } else if (email == "") {
            Toast.makeText(this@RegisterActivity, "Please write Email.", Toast.LENGTH_LONG).show()

        } else if (password == "") {
            Toast.makeText(this@RegisterActivity, "Please write Password.", Toast.LENGTH_LONG)
                .show()

        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                            .child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["profile"] =
                            "https://firebasestorage.googleapis.com/v0/b/orientconnect-b0ac2.appspot.com/o/profile.png?alt=media&token=d1b8f6cf-4972-4f8a-a6d7-72041965118f"
                        userHashMap["cover"] =
                            "https://firebasestorage.googleapis.com/v0/b/orientconnect-b0ac2.appspot.com/o/cover.jpg?alt=media&token=fb5c9186-ff74-47b8-be11-9444a08e0dcb"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["website"] = "https://www.google.com"

                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error message" + task.exception!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
        }


    }


}