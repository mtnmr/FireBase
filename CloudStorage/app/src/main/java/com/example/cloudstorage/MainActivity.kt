package com.example.cloudstorage

import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import coil.load
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {

    private lateinit var storage:FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = Firebase.storage

        val button = findViewById<Button>(R.id.download_button)
        button.setOnClickListener {
            val storageRef = storage.reference
            val pathReference = storageRef.child("image/QTEzuTaZPWiC3fpxTl52A81G7RhuZMaxAjRzubIc.jpeg")

            val ONE_MEGABYTE: Long = 1024 * 1024
            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                val bitmap =  BitmapFactory.decodeByteArray(it, 0,  it.size)
                findViewById<ImageView>(R.id.download_image).setImageBitmap(bitmap)

                Log.d("cloud storage", "download success")
            }.addOnFailureListener {
                Log.d("cloud storage", "download failure $it")
            }
        }
    }
}