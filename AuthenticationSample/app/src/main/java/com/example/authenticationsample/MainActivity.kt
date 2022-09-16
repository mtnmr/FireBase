package com.example.authenticationsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.authenticationsample.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signupButton.setOnClickListener {
            val email: String = binding.emailFieldText.text.toString()
            val password: String = binding.passwordFieldText.text.toString()
            userSignUp(email, password)
        }

        binding.loginButton.setOnClickListener {
            val email: String = binding.emailFieldText.text.toString()
            val password: String = binding.passwordFieldText.text.toString()
            userLogin(email, password)
        }
    }

    override fun onStart() {
        super.onStart()

        //アクティビティを初期化する時に、ユーザーが現在ログインしているか確認する
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //ログインしていたらページのリロードができる、とかの処理
        }
    }

    private fun userSignUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "SignUP Success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "SignUP Failure", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun userLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, NextActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show()
                }
            }
    }
}