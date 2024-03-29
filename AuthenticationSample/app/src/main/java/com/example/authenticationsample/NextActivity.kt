package com.example.authenticationsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.authenticationsample.databinding.ActivityNextBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class NextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ユーザー情報の取得
        val user = Firebase.auth.currentUser
        val name = user?.displayName
        binding.username.text = getString(R.string.current_username, name)
        val email = user?.email
        binding.email.text = getString(R.string.current_email, email)
        val uid = user?.uid
        binding.uid.text = getString(R.string.current_uid, uid)

        //確認メールを送信する(コンソールでテンプレートを設定できる)
        binding.sendEmailButton.setOnClickListener {
            user?.sendEmailVerification()?.addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "email send", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.userSignoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}