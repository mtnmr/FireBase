package com.example.cloudstorage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var storage:FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = Firebase.storage

        val downloadButton = findViewById<Button>(R.id.download_button)
        downloadButton.setOnClickListener {
            downloadImage()
        }

        val uploadButton = findViewById<Button>(R.id.upload_button)
        uploadButton.setOnClickListener {
            checkOpenDocumentPermission()
        }
    }

    private fun downloadImage(){
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

    private fun checkOpenDocumentPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            selectPhoto()
        }else{
            openDocumentLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private val openDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted:Boolean ->
            if(isGranted){
                selectPhoto()
            }else{
                Toast.makeText(this, "デバイス内の写真やメディアへのアクセスが許可されませんでした。", Toast.LENGTH_SHORT).show()
            }
        }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        selectPhotoLauncher.launch(intent)
    }

    private val selectPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode != RESULT_OK){
                return@registerForActivityResult
            }else{
                try {
                    var bitmap: Bitmap?
                    result.data?.data.also { uri ->
                        bitmap =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                                val source = ImageDecoder.createSource(contentResolver, uri!!)
                                ImageDecoder.decodeBitmap(source)
                            } else {
                                MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            }
                        findViewById<ImageView>(R.id.download_image).setImageBitmap(bitmap)

                        if(uri != null) uploadImage(uri)
                    }
                }catch (e:Exception){
                    Log.d("cloud storage", e.toString())
                    Toast.makeText(this, "select photo Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun uploadImage(uri: Uri) {
        val storageRef = storage.reference
//        var file = Uri.fromFile(File(uri?.path.toString()))
//        val userImageRef = storageRef.child("image/${file.lastPathSegment}")
//        val uploadTask = userImageRef.putFile(file)
//        val path = uri?.path?: return
//        val stream = FileInputStream(File(path))
        val stream = contentResolver.openInputStream(uri)
        val userImageRef = storageRef.child("image/${uri.path}")
        val uploadTask = userImageRef.putStream(stream!!)
        uploadTask.addOnSuccessListener {
            Log.d("cloud storage", "upload success")
        }.addOnFailureListener { error ->
            Log.d("cloud storage", "upload failure $error")
            }
    }

}