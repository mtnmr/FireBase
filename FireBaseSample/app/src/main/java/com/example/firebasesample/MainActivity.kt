package com.example.firebasesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebasesample.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore

        binding.addButton.setOnClickListener {
            val task = Task(
                title = binding.titleEditText.text.toString()
            )

            db.collection("tasks")
                .add(task)
                .addOnSuccessListener { documentReference ->
                    Log.d("ADD","DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("ADD", "Error adding document", e)
                }
        }

        val taskAdapter = TaskAdapter()
        binding.recyclerView.adapter = taskAdapter

        db.collection("tasks")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { tasks ->
                val taskList = ArrayList<Task>()
                tasks.forEach { taskList.add(it.toObject(Task::class.java)) }
                taskAdapter.submitList(taskList)
            }
            .addOnFailureListener {exception ->
                Log.d("READ", "Error getting documents: ", exception)
            }


        db.collection("tasks")
            .orderBy("createdAt")
            .addSnapshotListener{ tasks, e ->
            if (e != null){
                Log.w("READ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (tasks != null){
                val taskList = ArrayList<Task>()
                tasks.forEach { taskList.add(it.toObject(Task::class.java)) }
                taskAdapter.submitList(taskList)
            }else{
                Log.d("READ", "Current data: null")
            }
        }
    }
}